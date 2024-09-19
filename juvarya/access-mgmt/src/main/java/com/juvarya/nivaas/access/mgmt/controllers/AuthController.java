package com.juvarya.nivaas.access.mgmt.controllers;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;

import org.apache.commons.lang3.ObjectUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.juvarya.nivaas.access.mgmt.dto.RefreshTokenRequest;
import com.juvarya.nivaas.access.mgmt.dto.request.LoginRequest;
import com.juvarya.nivaas.access.mgmt.jwt.JwtResponse;
import com.juvarya.nivaas.access.mgmt.model.CustomerLastLoginModel;
import com.juvarya.nivaas.access.mgmt.model.MediaModel;
import com.juvarya.nivaas.access.mgmt.model.Role;
import com.juvarya.nivaas.access.mgmt.model.User;
import com.juvarya.nivaas.access.mgmt.model.UserOTPModel;
import com.juvarya.nivaas.access.mgmt.services.CustomerLastLoginService;
import com.juvarya.nivaas.access.mgmt.services.MediaService;
import com.juvarya.nivaas.access.mgmt.services.RoleService;
import com.juvarya.nivaas.access.mgmt.services.UserOTPService;
import com.juvarya.nivaas.access.mgmt.services.UserService;
import com.juvarya.nivaas.access.mgmt.services.impl.UserDetailsServiceImpl;
import com.juvarya.nivaas.auth.JwtUtils;
import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.commonservice.dto.LoggedInUser;
import com.juvarya.nivaas.commonservice.dto.MessageResponse;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings({ "rawtypes" })
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/nivaas/auth")
@Slf4j
public class AuthController extends JTBaseEndpoint {
	private static final String AUTHORIZATION = "Authorization";
	private static final String SIGN_IN = "SIGNIN";
	private static final long OTP_VALID_DURATION = 15 * 60 * 1000;

	@Autowired
	private UserOTPService userOTPService;

	@Autowired
	private UserService userService;

	@Autowired
	private RoleService roleService;

	@Autowired
	private JwtUtils jwtUtils;

	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	@Autowired
	private CustomerLastLoginService customerLastLoginService;
	
	@Autowired
	private MediaService mediaService;

	@CrossOrigin(origins = "*")
	@PostMapping("/signin")
	public ResponseEntity<Object> authenticateUser(@Valid @RequestBody LoginRequest loginRequest)
			throws JsonProcessingException {
		log.info("Entering authenticateUser with Primary Contact: {}", loginRequest.getPrimaryContact());

		if (null != loginRequest.getPrimaryContact() && !loginRequest.getPrimaryContact().isEmpty()) {

			UserOTPModel userOTPModel = userOTPService.findByOtpTypeAndPrimaryContact(SIGN_IN,
					loginRequest.getPrimaryContact());
			if (!Objects.nonNull(userOTPModel)) {
				log.warn("Unable to find OTP for Primary Contact: {}", loginRequest.getPrimaryContact());

				return ResponseEntity.badRequest().body(new MessageResponse("Unable to find otp"));
			} else if (validateOtpTime(userOTPModel)) {
				log.warn("OTP expired for Primary Contact: {}", loginRequest.getPrimaryContact());

				return ResponseEntity.badRequest().body(new MessageResponse("Otp expired"));
			}

			if (!loginRequest.getOtp().equals(userOTPModel.getOtp())
					|| !loginRequest.getPrimaryContact().equals(userOTPModel.getPrimaryContact())) {
				log.error("Invalid OTP for Primary Contact: {}", loginRequest.getPrimaryContact());

				throw new BadCredentialsException("Invalid OTP");

			} else {
				userOTPService.deleteOTP(userOTPModel);
				log.info("Deleted OTP for Primary Contact: {}", loginRequest.getPrimaryContact());
			}
			User user = userDetailsService.loadUserByPrimaryContact(loginRequest.getPrimaryContact());
			if (ObjectUtils.isEmpty(user)) {
				log.info("No user found for Primary Contact: {}. Creating new user.", loginRequest.getPrimaryContact());

				user = new User();
				user.setPrimaryContact(loginRequest.getPrimaryContact());
				Set<Role> userRoles = new HashSet<>();
				userRoles.add(roleService.findByErole(ERole.ROLE_USER));
				user.setRoles(userRoles);
				user.setCreationTime(new Date());
				userService.saveUser(user);
				log.info("New user created with Primary Contact: {}", user.getPrimaryContact());

				LoggedInUser loggedInUser = convertToLoggedInUser(user);
				String jwt = jwtUtils.generateJwtToken(loggedInUser);
				String refreshToken = jwtUtils.generateRefreshToken(loggedInUser);

				List<String> roles = user.getRoles().stream().map(role -> role.getName().name())
						.collect(Collectors.toList());
				log.info("Returning JWT response for new user with Primary Contact: {}", user.getPrimaryContact());

				return ResponseEntity.ok(new JwtResponse(jwt, loggedInUser.getId(), loggedInUser.getPrimaryContact(),
						loggedInUser.getEmail(), roles, refreshToken));

			} else {
				log.info("Existing user found for Primary Contact: {}", user.getPrimaryContact());

				LoggedInUser loggedInUser = convertToLoggedInUser(user);

				String jwt = jwtUtils.generateJwtToken(loggedInUser);
				String refreshToken = jwtUtils.generateRefreshToken(loggedInUser);

				List<String> roles = user.getRoles().stream().map(role -> role.getName().name())
						.collect(Collectors.toList());
				log.info("Returning JWT response for existing user with Primary Contact: {}", user.getPrimaryContact());

				return ResponseEntity.ok(new JwtResponse(jwt, loggedInUser.getId(), loggedInUser.getPrimaryContact(),
						loggedInUser.getEmail(), roles, refreshToken));
			}

		}
		log.warn("Required PrimaryContact not provided in request");
		return ResponseEntity.badRequest().body(new MessageResponse("Required PrimaryContact"));
	}

	private LoggedInUser convertToLoggedInUser(User user) {
		LoggedInUser loggedInUser = new LoggedInUser();
		loggedInUser.setEmail(user.getEmail());
		loggedInUser.setId(user.getId());
		loggedInUser.setPrimaryContact(user.getPrimaryContact());
		loggedInUser.setFullName(user.getFullName());
		List<String> roles = user.getRoles().stream().map(role -> role.getName().name()).collect(Collectors.toList());
		loggedInUser.setRoles(new HashSet<>(roles));
		loggedInUser.setVersion(user.getVersion());
		log.debug("Converted User to LoggedInUser for Primary Contact: {}", user.getPrimaryContact());
		return loggedInUser;
	}

	@GetMapping("/currentCustomer")
	public ResponseEntity<LoggedInUser> getCurrentCustomer(HttpServletRequest request)
			throws JsonProcessingException, ParseException {
		log.info("Entering getCurrentCustomer method");

		String jwt = parseJwt(request);
		if (jwt != null && jwtUtils.validateJwtToken(jwt)) {
			log.info("JWT is valid");

			LoggedInUser loggedInUser = jwtUtils.getUserFromToken(jwt);
			User user = userService.findById(loggedInUser.getId());

			if (null != user) {
				log.info("User found with ID: {}", loggedInUser.getId());

				CustomerLastLoginModel customerLastLoginModel = new CustomerLastLoginModel();
				CustomerLastLoginModel customerLastLogin = customerLastLoginService.findByJtCustomer(user);

				if (Objects.isNull(customerLastLogin)) {
					log.info("Customer last login not found, creating a new one");
					Date date = currentDate();
					customerLastLoginModel.setDate(date);
					customerLastLoginModel.setCustomer(user);
					customerLastLoginService.save(customerLastLoginModel);
				} else {
					log.info("Customer last login found, updating the date");
					Date date = currentDate();
					customerLastLogin.setDate(date);
					customerLastLogin.setCustomer(user);
					customerLastLoginService.save(customerLastLogin);
				}

				loggedInUser.setNewUser(Boolean.TRUE);

				if (null != user.getFullName()) {
					loggedInUser.setNewUser(Boolean.FALSE);
					loggedInUser.setFullName(user.getFullName());
				}

				if (null != user.getEmail()) {
					loggedInUser.setNewUser(Boolean.FALSE);
					loggedInUser.setEmail(user.getEmail());
				}
				
				MediaModel mediaModel = mediaService.findByJtcustomerAndMediaType(user.getId(), "PROFILE_PICTURE");
				if(Objects.nonNull(mediaModel)) {
					loggedInUser.setProfilePicture(mediaModel.getUrl());
				}
				
				log.info("Returning logged in user information for user ID: {}", loggedInUser.getId());
				return new ResponseEntity<>(loggedInUser, HttpStatus.OK);
			} else {
				log.warn("User not found with ID: {}", loggedInUser.getId());
			}
		} else {
			log.warn("Invalid or missing JWT");
		}
		return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
	}

	private String parseJwt(HttpServletRequest request) {
		String headerAuth = request.getHeader(AUTHORIZATION);
		if (StringUtils.isNoneEmpty(headerAuth) && headerAuth.startsWith("Bearer ")) {
			return headerAuth.substring(7, headerAuth.length());
		}
		log.warn("JWT token is missing or does not start with Bearer");
		return null;
	}

	private Date currentDate() throws ParseException {
		LocalDateTime localDateTime = LocalDateTime.now(ZoneId.of("Asia/Kolkata"));
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd hh:mm:ss");
		String formattedTime = localDateTime.format(formatter);
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
		return dateFormat.parse(formattedTime);
	}

	private boolean validateOtpTime(UserOTPModel userOtpModel) {
		long currentTimeInMillis = System.currentTimeMillis();
		long otpCreationTimeInMillis = userOtpModel.getCreationTime().getTime();
		return otpCreationTimeInMillis + OTP_VALID_DURATION <= currentTimeInMillis;
	}
	
	@PostMapping("/refreshToken")
	public ResponseEntity<Object> refreshToken(@RequestBody @Valid RefreshTokenRequest refreshTokenRequest) throws JsonProcessingException {
	        String refreshToken = refreshTokenRequest.getRefreshToken();
	        if (refreshToken != null && jwtUtils.validateJwtToken(refreshToken)) {
	        // Extract user details from refresh token
	        LoggedInUser userDetailsFromToken = jwtUtils.getUserFromToken(refreshToken);
	        User user = userService.findById(userDetailsFromToken.getId());
			LoggedInUser loggedInUser = convertToLoggedInUser(user);

	        // Generate new access and refresh tokens
	        String newAccessToken = jwtUtils.generateJwtToken(loggedInUser);
	        
			return ResponseEntity
					.ok(new JwtResponse(newAccessToken, loggedInUser.getId(), loggedInUser.getPrimaryContact(),
							loggedInUser.getEmail(), new ArrayList<>(loggedInUser.getRoles()), refreshToken));
		}
		throw new NivaasCustomerException(ErrorCode.TOKEN_PROCESSING_ERROR);
	}


}
