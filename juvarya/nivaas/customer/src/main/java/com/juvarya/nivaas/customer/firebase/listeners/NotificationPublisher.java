package com.juvarya.nivaas.customer.firebase.listeners;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class NotificationPublisher {

	@Autowired
	private ApplicationEventPublisher applicationEventPublisher;

	public void sendNotification(Long apartment, Long user, boolean apartmentOnboard, boolean apartmentApprove,
			Long flat, boolean flatOnboard, boolean flatApprove, Long societyDue, String isPaid, double totalCost,
			boolean due, Long tenant) {

		log.info("Sending notification in Async");
		Notification notification = new Notification(this);
		notification.setApartment(apartment);
		notification.setApartmentOnboard(apartmentOnboard);
		notification.setApartmentApprove(apartmentApprove);
		notification.setFlat(flat);
		notification.setFlatOnboard(flatOnboard);
		notification.setFlatApprove(flatApprove);
		notification.setSocietyDue(societyDue);
		notification.setPaid(isPaid);
		//notification.setToken(token);
		notification.setCustomer(user);
		notification.setTotalCost(totalCost);
		notification.setDue(due);
		notification.setTenat(tenant);
		applicationEventPublisher.publishEvent(notification);
	}
}
