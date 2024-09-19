package com.juvarya.nivaas.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.customer.model.NotificationModel;

@Repository
public interface NotificationRepository extends JpaRepository<NotificationModel, Long> {
	Page<NotificationModel> findByUserIdOrTenantId(Long user, Long tenant, Pageable pageable);

}
