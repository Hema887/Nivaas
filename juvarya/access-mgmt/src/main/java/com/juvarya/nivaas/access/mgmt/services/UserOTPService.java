package com.juvarya.nivaas.access.mgmt.services;

import com.juvarya.nivaas.access.mgmt.model.UserOTPModel;

import java.util.List;

public interface UserOTPService {
	UserOTPModel save(UserOTPModel userOTP);

	UserOTPModel findByEmailAddressAndOtpType(String emailAddress, String otpType);

	void deleteOTP(UserOTPModel userOtp);

	List<UserOTPModel> findByPrimaryContactAndOtpType(String primaryContact, String otpType);

	UserOTPModel findByOtpTypeAndPrimaryContact(String otpType, String primaryContact);
}
