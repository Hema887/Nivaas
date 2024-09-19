package com.juvarya.nivaas.customer.service.impl;

import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.juvarya.nivaas.customer.model.NotificationModel;
import com.juvarya.nivaas.customer.repository.NotificationRepository;
import com.juvarya.nivaas.customer.service.NotificationService;

@Service
public class NotificationServiceImpl implements NotificationService {

	@Autowired
	private NotificationRepository notificationRepository;

	@Transactional
	@Override
	public NotificationModel save(NotificationModel jtNotification) {
		return notificationRepository.save(jtNotification);
	}

	@Transactional
	@Override
	public List<NotificationModel> saveAll(List<NotificationModel> notificationModels) {
		return notificationRepository.saveAll(notificationModels);
	}

	@Override
	public NotificationModel findById(Long id) {
		Optional<NotificationModel> notification = notificationRepository.findById(id);
		return notification.orElse(null);

	}

	@Transactional
	@Override
	public void removeNotification(NotificationModel notificationModel) {
		notificationRepository.delete(notificationModel);

	}

	@Override
	public Page<NotificationModel> getAll(Pageable pageable) {
		return notificationRepository.findAll(pageable);
	}

	@Override
	public Page<NotificationModel> findByUserOrTenant(Long userId, Long tenantId, Pageable pageable) {
		return notificationRepository.findByUserIdOrTenantId(userId, tenantId, pageable);

	}

}
