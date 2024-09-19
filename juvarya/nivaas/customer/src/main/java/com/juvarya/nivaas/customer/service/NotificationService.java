package com.juvarya.nivaas.customer.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.juvarya.nivaas.customer.model.NotificationModel;

import java.util.List;

public interface NotificationService {
	
	NotificationModel save(NotificationModel jtNotification);

	List<NotificationModel> saveAll(List<NotificationModel> notificationModels);
	
	NotificationModel findById(Long id);
	
	void removeNotification(NotificationModel notificationModel);
	
	Page<NotificationModel> getAll (Pageable pageable);
	
	Page<NotificationModel> findByUserOrTenant(Long userId, Long tenantId, Pageable pageable);
	

}
