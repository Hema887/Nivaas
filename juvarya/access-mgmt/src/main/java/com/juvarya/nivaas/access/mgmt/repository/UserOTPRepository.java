package com.juvarya.nivaas.access.mgmt.repository;

import com.juvarya.nivaas.access.mgmt.model.UserOTPModel;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface UserOTPRepository extends JpaRepository<UserOTPModel, Long> {
	List<UserOTPModel> findByEmailAddressAndOtpType(String emailAddress, String otpType, Sort sort);

	List<UserOTPModel> findByPrimaryContactAndOtpType(String primaryContact, String otpType);

	UserOTPModel findByOtpTypeAndPrimaryContact(String otpType, String primaryContact);
}
