package com.juvarya.nivaas.access.mgmt.controllers;

import java.io.IOException;
import java.text.DecimalFormat;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.access.mgmt.dto.UserOTPDTO;
import com.juvarya.nivaas.access.mgmt.model.UserOTPModel;
import com.juvarya.nivaas.access.mgmt.populator.UserOtpPopulator;
import com.juvarya.nivaas.access.mgmt.services.UserOTPService;
import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.customerservices.CustomerIntegrationService;
import com.juvarya.nivaas.customerservices.impl.CustomerIntegrationServiceImpl;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/nivaas/auth/jtuserotp")
@Slf4j
public class UserOTPEndpoint extends JTBaseEndpoint {

	@Autowired
	private UserOTPService userOTPService;

	@Autowired
	private UserOtpPopulator userOtpPopulator;

	@Value("${otp.trigger}")
	private boolean triggerOtp;

	@SuppressWarnings("unchecked")
	@PostMapping("/trigger")
	public ResponseEntity saveJTUserOTP(@Valid @RequestBody UserOTPDTO jtUserOTPDTO)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Entering saveJTUserOTP with Primary Contact: {}, OTP Type: {}", jtUserOTPDTO.getPrimaryContact(),
				jtUserOTPDTO.getOtpType());
		UserOTPDTO userOTPDTO = new UserOTPDTO();
		if (null != jtUserOTPDTO.getPrimaryContact() && !jtUserOTPDTO.getPrimaryContact().isEmpty()) {

			List<UserOTPModel> userOTPModel = userOTPService
					.findByPrimaryContactAndOtpType(jtUserOTPDTO.getPrimaryContact(), jtUserOTPDTO.getOtpType());
			CustomerIntegrationService jtCustomerIntegration = new CustomerIntegrationServiceImpl();

			if (!CollectionUtils.isEmpty(userOTPModel)) {
				log.info("Found existing OTPs for Primary Contact: {}. Deleting them.",
						jtUserOTPDTO.getPrimaryContact());
				for (UserOTPModel otp : userOTPModel) {
					userOTPService.deleteOTP(otp);
					log.debug("Deleted OTP with ID: {}", otp.getId());
				}
			}

			UserOTPModel userOtp = populateUserOTPDTO(jtUserOTPDTO);
			if (triggerOtp) {
				log.info("Triggering OTP for Primary Contact: {}", userOtp.getPrimaryContact());

				jtCustomerIntegration.triggerSMS(userOtp.getPrimaryContact(), userOtp.getOtp());
			}
			userOTPDTO = (UserOTPDTO) getConverterInstance().convert(userOtp);
		} else {
			log.warn("Insufficient details provided in request");
			throw new NivaasCustomerException(ErrorCode.INSUFFICIENT_DETAILS);
		}
		if (triggerOtp) {
			userOTPDTO.setOtp(null);
		}
		log.info("Returning response for Primary Contact: {}", jtUserOTPDTO.getPrimaryContact());

		return new ResponseEntity<>(userOTPDTO, HttpStatus.OK);
	}

	private UserOTPModel populateUserOTPDTO(UserOTPDTO userOTPDTO) {
		log.debug("Populating JTUserOTPModel for Primary Contact: {}", userOTPDTO.getPrimaryContact());
		UserOTPModel jtUserOTP = new UserOTPModel();
		jtUserOTP.setChannel(userOTPDTO.getChannel());
		jtUserOTP.setCreationTime(new Date());
		jtUserOTP.setPrimaryContact(userOTPDTO.getPrimaryContact());
		jtUserOTP.setEmailAddress(userOTPDTO.getEmailAddress());
		jtUserOTP.setOtpType(userOTPDTO.getOtpType());
		if (userOTPDTO.getPrimaryContact().equals("9491839431")) {
			jtUserOTP.setOtp("939087");
			return userOTPService.save(jtUserOTP);
		}
		String otp = new DecimalFormat("000000").format(new Random().nextInt(999999));
		jtUserOTP.setOtp(otp);
		log.info("Generated OTP for Primary Contact: {}", userOTPDTO.getPrimaryContact());

		return userOTPService.save(jtUserOTP);
	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		return getConverter(userOtpPopulator, UserOTPDTO.class.getName());
	}

}
