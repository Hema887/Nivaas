package com.juvarya.nivaas.customer.firebase.listeners;

import java.util.List;
import java.util.Objects;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.juvarya.nivaas.auth.JwtUtils;
import com.juvarya.nivaas.commonservice.dto.NotificationRequest;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.customer.client.AccessMgmtClient;
import com.juvarya.nivaas.customer.client.FeignRequestContext;
import com.juvarya.nivaas.customer.firebase.FCMService;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.customer.model.SocietyDue;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.service.ApartmentUserRoleService;
import com.juvarya.nivaas.customer.service.NivaasFlatService;
import com.juvarya.nivaas.customer.service.SocietyDueService;

import lombok.extern.slf4j.Slf4j;

@EnableAsync
@Component
@Slf4j
public class NotificationListener implements ApplicationListener<Notification> {

	@Autowired
	private FCMService fcmService;

	@Autowired
	private NivaasApartmentService nivaasApartmentService;

	@Autowired
	private NivaasFlatService nivaasFlatService;

	@Autowired
	private ApartmentUserRoleService apartmentUserRoleService;

	@Autowired
	private AccessMgmtClient accessMgmtClient;

	@Autowired
	private SocietyDueService societyDueService;

	@Autowired
	private JwtUtils jwtUtils;

	@Override
	public void onApplicationEvent(Notification event) {

		try {
			NotificationRequest notificationRequest = new NotificationRequest();
			String systemUserToken = jwtUtils.getSystemUserToken();
			FeignRequestContext.setAuthorizationHeader("Bearer " + systemUserToken);

			if (null != event.getFlat() && Boolean.TRUE.equals(event.isFlatOnboard())
					&& Boolean.FALSE.equals(event.isFlatApprove()) && null != event.getCustomer()) {

				NivaasFlatModel nivaasFlatModel = nivaasFlatService.findById(event.getFlat());

				if (Objects.nonNull(nivaasFlatModel)) {

					List<ApartmentUserRoleModel> apartmentUserRoleModels = apartmentUserRoleService
							.getByApartmentModel(nivaasFlatModel.getApartment());

					if (!CollectionUtils.isEmpty(apartmentUserRoleModels)) {
						for (ApartmentUserRoleModel apartmentUserRoleModel : apartmentUserRoleModels) {
							UserDTO apartmentAdmin = accessMgmtClient
									.getUserById(apartmentUserRoleModel.getCustomerId());
							UserDTO user = accessMgmtClient.getUserById(event.getCustomer());

							notificationRequest.setTitle(apartmentUserRoleModel.getApartmentModel().getName() + " "
									+ nivaasFlatModel.getFlatNo());
							notificationRequest.setBody(user.getFullName() + " " + ",Raised Flat Onboard Request");
							notificationRequest.setToken(apartmentAdmin.getFcmToken());

							sendNotification(notificationRequest);
						}
					}
				}
			}

			if (null != event.getFlat() && null != event.getApartment() && Boolean.TRUE.equals(event.isFlatOnboard())
					&& Boolean.TRUE.equals(event.isFlatApprove())) {
				NivaasFlatModel nivaasFlatModel = nivaasFlatService.findById(event.getFlat());
				NivaasApartmentModel nivaasApartmentModel = nivaasApartmentService.findById(event.getApartment());

				if (Objects.nonNull(nivaasFlatModel) && Objects.nonNull(nivaasApartmentModel)
						&& nivaasFlatModel.getApartment().equals(nivaasApartmentModel)) {
					List<ApartmentUserRoleModel> apartmentUserRoleModels = apartmentUserRoleService
							.getByApartmentModel(nivaasFlatModel.getApartment());

					if (!CollectionUtils.isEmpty(apartmentUserRoleModels)) {
						for (ApartmentUserRoleModel apartmentUserRoleModel : apartmentUserRoleModels) {
							UserDTO flatOwner = accessMgmtClient.getUserById(event.getCustomer());

							notificationRequest.setTitle(apartmentUserRoleModel.getApartmentModel().getName() + " "
									+ nivaasFlatModel.getFlatNo());
							notificationRequest
									.setBody(flatOwner.getFullName() + " " + ",NIVAAS Admin Approved Your Flat");
							notificationRequest.setToken(flatOwner.getFcmToken());

							sendNotification(notificationRequest);
						}
					}
				}
			}

			if (Boolean.TRUE.equals(event.isDue()) && null != event.getSocietyDue()) {
				SocietyDue societyDue = societyDueService.findById(event.getSocietyDue());

				if (Objects.nonNull(societyDue)) {
					NivaasFlatModel nivaasFlatModel = nivaasFlatService.findById(societyDue.getFlatId());
					NivaasApartmentModel nivaasApartmentModel = nivaasApartmentService
							.findById(societyDue.getApartmentId());

					UserDTO user = accessMgmtClient.getUserById(event.getCustomer());

					notificationRequest.setTitle(nivaasApartmentModel.getName() + " " + nivaasFlatModel.getFlatNo());
					notificationRequest.setBody(user.getFullName() + " " + ", you have pending dues. Please pay the"
							+ " " + event.getTotalCost() + " " + "for your flat:" + " " + nivaasFlatModel.getFlatNo());
					notificationRequest.setToken(user.getFcmToken());

					sendNotification(notificationRequest);
				}

			}
		} catch (Exception e) {
			log.error("Failed to send notification ", e);
		} finally {
			FeignRequestContext.clearAuthorizationHeader();
		}

	}

	private void sendNotification(NotificationRequest notificationRequest) {
		try {
			fcmService.sendMessageToToken(notificationRequest);
		} catch (Exception e) {
		}
	}

}
