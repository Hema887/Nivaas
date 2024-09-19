package com.juvarya.nivaas.access.mgmt.populator;

import org.springframework.stereotype.Component;

import com.juvarya.nivaas.access.mgmt.dto.UserOTPDTO;
import com.juvarya.nivaas.access.mgmt.model.UserOTPModel;
import com.juvarya.nivaas.utils.converter.Populator;

@Component
public class UserOtpPopulator implements Populator<UserOTPModel, UserOTPDTO> {

	@Override
	public void populate(UserOTPModel source, UserOTPDTO target) {
		target.setId(source.getId());
		target.setCreationTime(source.getCreationTime());
		target.setOtpType(source.getOtpType());
		target.setOtp(source.getOtp());
	}

}
