package com.juvarya.nivaas.customer.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.customer.dto.JTMaintenanceDTO;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;
import com.juvarya.nivaas.customer.model.MaintenanceModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.PrepaidFlatUsageModel;
import com.juvarya.nivaas.customer.model.PrepaidMeterModel;
import com.juvarya.nivaas.customer.populator.MaintenancePopulator;
import com.juvarya.nivaas.customer.repository.MaintenanceRepository;
import com.juvarya.nivaas.customer.repository.PrepaidFlatUsageRepository;
import com.juvarya.nivaas.customer.repository.PrepaidMeterRepository;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.service.ApartmentUserRoleService;
import com.juvarya.nivaas.customer.service.MaintenanceService;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.SecurityUtils;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@Transactional
@SuppressWarnings("rawtypes")
@Service
@Slf4j
public class MaintenanceServiceImpl extends JTBaseEndpoint implements MaintenanceService {

	@Autowired
	private MaintenanceRepository maintenanceRepository;

	@Autowired
	private PrepaidMeterRepository prePaidMeterRepository;

	@Autowired
	private ApartmentUserRoleService apartmentUserRoleService;

	@Autowired
	private NivaasApartmentService nivaasApartmentService;

	@Autowired
	private MaintenancePopulator maintenancePopulator;
	
	@Autowired
	private PrepaidFlatUsageRepository prepaidFlatUsageRepository;

	@Override
	public List<PrepaidMeterModel> getPrepaid(NivaasApartmentModel apartment) {
		log.info("Fetching prepaid meters for apartment {}", apartment.getId());
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();

		ApartmentUserRoleModel apartmentUserRoleModel = apartmentUserRoleService
				.findByApartmentModelAndJtCustomer(apartment, loggedInUser.getId());

		if (apartmentUserRoleModel.getRoleName().equals(NivaasConstants.ROLE_APARTMENT_ADMIN)) {
			List<PrepaidMeterModel> prepaidList = prePaidMeterRepository.getByApartmentModel(apartment);
			if (Objects.nonNull(prepaidList)) {
				return prepaidList;
			}
			return null;
		}
		return null;
	}

	@Override
	@Transactional
	public ResponseEntity create(JTMaintenanceDTO jtMaintenanceDTO) {
		log.info("Creating maintenance for apartment {}", jtMaintenanceDTO.getApartmentId());
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();

		NivaasApartmentModel nivaasApartmentModel = nivaasApartmentService.findById(jtMaintenanceDTO.getApartmentId());

		if (Objects.isNull(nivaasApartmentModel)) {
			log.warn("Apartment not found for ID: {}", jtMaintenanceDTO.getApartmentId());
			throw new NivaasCustomerException(ErrorCode.APARTMENT_NOT_FOUND);
		}

		ApartmentUserRoleModel apartmentUserRoleModel = apartmentUserRoleService
				.findByApartmentModelAndJtCustomer(nivaasApartmentModel, loggedInUser.getId());

		if (null != apartmentUserRoleModel) {

			MaintenanceModel maintenanceModel = maintenanceRepository.findByApartmentModel(nivaasApartmentModel);
			if (Objects.nonNull(maintenanceModel)) {
				log.debug("Updating existing maintenance record");

				List<PrepaidMeterModel> prepaidMeters = prePaidMeterRepository.findByMaintenanceId(maintenanceModel.getId());
				if(!CollectionUtils.isEmpty(prepaidMeters)) {
					for(PrepaidMeterModel meter : prepaidMeters) {
						meter.setMaintenance(null);
						prePaidMeterRepository.save(meter);
					}
				}

				if (null != jtMaintenanceDTO.getCost()) {
					maintenanceModel.setCost(jtMaintenanceDTO.getCost());
				}
				maintenanceModel.setCreationTime(new Date());
				maintenanceModel.setApartmentModel(nivaasApartmentModel);
				maintenanceModel.setNotifyOn(jtMaintenanceDTO.getNotifyOn());
				populatePrepaidMetersAndSaveMaintenanceModel(jtMaintenanceDTO.getPrepaidId(), maintenanceModel);
				log.info("Maintena	nce Updated");
				return ResponseEntity.ok(new MessageResponse("Maintenance Updated"));

			} else {
				log.debug("Creating new maintenance record");
				MaintenanceModel jtMaintenanceModel = new MaintenanceModel();
				jtMaintenanceModel.setCost(jtMaintenanceDTO.getCost());
				jtMaintenanceModel.setCreationTime(new Date());
				jtMaintenanceModel.setApartmentModel(nivaasApartmentModel);
				jtMaintenanceModel.setNotifyOn(jtMaintenanceDTO.getNotifyOn());
				populatePrepaidMetersAndSaveMaintenanceModel(jtMaintenanceDTO.getPrepaidId(), jtMaintenanceModel);
				log.info("Maintenance Added");
				return ResponseEntity.ok(new MessageResponse("Maintenance Added"));
			}
		}
		log.warn("User is not allowed to add maintenance");
		throw new NivaasCustomerException(ErrorCode.MAINTENANCE_CREATE_NOT_ALLOWED);

	}

	private void populatePrepaidMetersAndSaveMaintenanceModel(List<Long> prepaidIds,
			MaintenanceModel maintenanceModel) {
		List<PrepaidMeterModel> prepaidMeterModels = new ArrayList<>();
		if(!CollectionUtils.isEmpty(prepaidIds)) {
		for (Long prePaidId : prepaidIds) {
			if (null != prePaidId) {
				Optional<PrepaidMeterModel> jtPrePaidMeterModel = prePaidMeterRepository.findById(prePaidId);
				if (jtPrePaidMeterModel.isPresent()) {
					PrepaidMeterModel prepaidMeterModel = jtPrePaidMeterModel.get();
					List<PrepaidFlatUsageModel> usages = prepaidFlatUsageRepository.findByPrepaidMeterId(prepaidMeterModel.getId());
		            if (!CollectionUtils.isEmpty(prepaidMeterModels)) {
		                usages.forEach(usage -> {
		                    usage.setPrepaidMeterId(null);
		                    prepaidFlatUsageRepository.save(usage);
		                    prepaidFlatUsageRepository.delete(usage);
		                });
		            }
					prepaidMeterModel.setMaintenance(maintenanceModel);
					prepaidMeterModels.add(jtPrePaidMeterModel.get());
				} else {
					log.warn("Prepaid Meter not found for ID: {}", prePaidId);
				}
			}
		}
		maintenanceModel.setMeters(prepaidMeterModels);
	}
	maintenanceRepository.save(maintenanceModel);
}

	@SuppressWarnings("unchecked")
	public JTMaintenanceDTO findByApartment(Long apartmentId)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		NivaasApartmentModel apartment = nivaasApartmentService.findById(apartmentId);
		if (Objects.isNull(apartment)) {
			return null;
		}
		MaintenanceModel maintenanceModel = maintenanceRepository.findByApartmentModel(apartment);
		JTMaintenanceDTO jtMaintenanceDTO = (JTMaintenanceDTO) getConverterInstance().convert(maintenanceModel);
		return jtMaintenanceDTO;
	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		return getConverter(maintenancePopulator, JTMaintenanceDTO.class.getName());
	}
}