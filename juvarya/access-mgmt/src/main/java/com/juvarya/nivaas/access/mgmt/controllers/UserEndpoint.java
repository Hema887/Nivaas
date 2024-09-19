package com.juvarya.nivaas.access.mgmt.controllers;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

import com.juvarya.nivaas.access.mgmt.dto.request.UserUpdateDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.juvarya.nivaas.access.mgmt.azure.service.AwsBlobService;
import com.juvarya.nivaas.access.mgmt.dto.BasicUserDetails;
import com.juvarya.nivaas.access.mgmt.model.MediaModel;
import com.juvarya.nivaas.access.mgmt.model.User;
import com.juvarya.nivaas.access.mgmt.populator.MediaPopulator;
import com.juvarya.nivaas.access.mgmt.populator.UserPopulator;
import com.juvarya.nivaas.access.mgmt.services.UserService;
import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.commonservice.dto.BasicOnboardUserDTO;
import com.juvarya.nivaas.commonservice.dto.MediaDTO;
import com.juvarya.nivaas.commonservice.dto.JTUserDTO;
import com.juvarya.nivaas.commonservice.dto.LoggedInUser;
import com.juvarya.nivaas.commonservice.dto.MessageResponse;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.SecurityUtils;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/user")
@Slf4j
public class UserEndpoint extends JTBaseEndpoint {
	@Autowired
	private UserService userService;

	@Autowired
	private AwsBlobService awsBlobService;

	@Autowired
	private MediaPopulator mediaPopulator;

	@Autowired
	private UserPopulator userPopulator;

	@GetMapping("/find/{userId}")
	public User getUser(@PathVariable("userId") Long userId) {
		log.info("Request received to fetch customer details for ID: {}", userId);

		return userService.findById(userId);
	}

	@PostMapping("/details/all")
	public List<User> getUserDetailsByIds(@RequestBody List<Long> userIds) {
		return userService.findByIds(userIds);
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/list/{role}")
	public ResponseEntity<Map<String, Object>> findCustomerByRoles(@PathVariable("role") String role,
			@RequestParam int page, @RequestParam int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		if (!StringUtils.hasText(role) && role.equalsIgnoreCase("role_admin")) {
			log.warn("Invalid role provided or role_admin is not allowed");
			return new ResponseEntity<>(null, HttpStatus.OK);
		}
		log.info("Entering into findCustomerByRoles API with role: {}", role);

		Pageable paginationRequest = PageRequest.of(page, pageSize);
		Page<User> users = userService.findByRolesContainsIgnoreCase(role, paginationRequest);

		Map<String, Object> response = new HashMap<>();
		response.put(NivaasConstants.CURRENT_PAGE, users.getNumber());
		response.put(NivaasConstants.TOTAL_ITEMS, users.getTotalElements());
		response.put(NivaasConstants.TOTAL_PAGES, users.getTotalPages());
		response.put(NivaasConstants.PAGE_NUM, page);
		response.put(NivaasConstants.PAGE_SIZE, pageSize);
		if (!CollectionUtils.isEmpty(users.getContent())) {
			for (User user : users.getContent()) {
				user.setPrimaryContact(null);
				user.setPostalCode(null);
			}
			response.put(NivaasConstants.PROFILES, getConverterInstance().convertAll(users.getContent()));
			log.info("Found {} users with role: {}", users.getContent().size(), role);
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		log.warn("No users found with role: {}", role);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PutMapping("/user/{userId}/role/{role}")
	public ResponseEntity addRole(@PathVariable("userId") Long userId, @PathVariable("role") ERole eRole) {
		userService.addUserRole(userId, eRole);
		return ResponseEntity.ok().body(new MessageResponse("User role update request processed"));
	}

	@PutMapping("/userDetails")
	public ResponseEntity basicUserDetails(@Valid @RequestBody BasicUserDetails details) {
		log.info("Entering into Update Basic User Details API");
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();

		User user = userService.findById(loggedInUser.getId());
		if (Objects.nonNull(user)) {
			log.info("Updating details for user with ID: {}", loggedInUser.getId());
			if (Objects.nonNull(details.getFullName()) && StringUtils.hasText(details.getFullName())) {
				user.setFullName(details.getFullName());
			}

			if (null != details.getEmail() && StringUtils.hasText(details.getEmail())) {
				if (!userService.existsByEmail(details.getEmail(), user.getId())) {
					user.setEmail(details.getEmail());
				} else {
					log.warn("Email '{}' is already in use", details.getEmail());
					throw new NivaasCustomerException(ErrorCode.EMAIL_ALREADY_IN_USE);
				}
			}
			if (null != details.getFcmToken()) {
				user.setFcmToken(details.getFcmToken());
			}

			userService.saveUser(user);
			log.info("User details updated successfully");
			return ResponseEntity.ok().body(new MessageResponse("Details Updated"));
		}
		log.error("User Not Found With Given Id" + " " + loggedInUser.getId());
		throw new NivaasCustomerException(ErrorCode.USER_NOT_FOUND);
	}

	@PutMapping("/{id}")
	public ResponseEntity updateUser(@PathVariable("id") @Valid @NotBlank Long id, @Valid @RequestBody UserUpdateDto details) {
		log.info("Entering into Update Basic User Details API");
		userService.updateUser(details, id);
		log.info("User details updated successfully");
		return ResponseEntity.ok().body(new MessageResponse("Details Updated"));
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/list")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity<Map<String, Object>> getUsersList(@RequestParam int pageNo, @Valid @RequestParam int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Fetching users list with pagination: pageNo={}, pageSize={}", pageNo, pageSize);
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<User> pageUsers = userService.getAllUsers(pageable);

		Map<String, Object> response = new HashMap<>();
		response.put(NivaasConstants.CURRENT_PAGE, pageUsers.getNumber());
		response.put(NivaasConstants.TOTAL_ITEMS, pageUsers.getTotalElements());
		response.put(NivaasConstants.TOTAL_PAGES, pageUsers.getTotalPages());
		response.put(NivaasConstants.PAGE_NUM, pageNo);
		response.put(NivaasConstants.PAGE_SIZE, pageSize);

		if (!CollectionUtils.isEmpty(pageUsers.getContent())) {

			List<JTUserDTO> dtos = getConverterInstance().convertAll(pageUsers.getContent());
			response.put(NivaasConstants.PROFILES, dtos);
			log.info("Users list fetched successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		log.info("No users found in the database");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@PostMapping("/onboard/user")
	public Long onBoardUser(@Valid @RequestBody BasicOnboardUserDTO basicOnboardUserDTO) {
		return userService.onBoardUser(basicOnboardUserDTO.getFullName(), basicOnboardUserDTO.getPrimaryContact(),
				basicOnboardUserDTO.getUserRoles());
	}

	@PostMapping("/save")
	public User saveUser(@RequestBody User user) {
		return userService.saveUser(user);
	}

	@DeleteMapping("/contact/{mobileNumber}/role/{role}")
	public void removeUserRole(@PathVariable("mobileNumber") String mobileNumber,
			@PathVariable("role") ERole userRole) {
		userService.removeUserRole(mobileNumber, userRole);
	}

	@GetMapping("/contact")
	public LoggedInUser getByPrimaryContact(@Valid @RequestParam("primaryContact") String primaryContact) {
		log.info("Request received to fetch customer details for primary Contact: {}", primaryContact);
		Optional<User> customer = userService.findByPrimaryContact(primaryContact);
		if (customer.isPresent()) {
			User user = customer.get();
			LoggedInUser loggedInUser = new LoggedInUser();
			loggedInUser.setId(user.getId());
			loggedInUser.setFullName(user.getFullName());
			List<String> roles = user.getRoles().stream().map(role -> role.getName().name())
					.collect(Collectors.toList());
			loggedInUser.setRoles(new HashSet<>(roles));
			loggedInUser.setPrimaryContact(user.getPrimaryContact());

			log.info("Customer details fetched successfully for Primary Contact: {}", primaryContact);
			return loggedInUser;
		}
		return null;

	}

	@SuppressWarnings("unchecked")
	@PostMapping("/upload")
	public ResponseEntity<MediaDTO> uploadCustomerProfilePicture(@ModelAttribute MultipartFile profilePicture)
			throws IOException, ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Entering uploadCustomerProfilePicture API");
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();

		if (!ObjectUtils.isEmpty(loggedInUser)) {
			MediaModel mediaModel = awsBlobService.uploadCustomerPictureFile(loggedInUser.getId(), profilePicture,
					loggedInUser.getId());
			MediaDTO mediaDTO = (MediaDTO) getConvertedInstance().convert(mediaModel);
			log.info("Profile picture uploaded successfully for user with ID {}", loggedInUser.getId());
			return new ResponseEntity<>(mediaDTO, HttpStatus.OK);
		}
		log.error("LoggedInUser not found or invalid");
		return new ResponseEntity<>(new MediaDTO(), HttpStatus.BAD_REQUEST);
	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConvertedInstance() {
		return getConverter(mediaPopulator, MediaDTO.class.getName());
	}

	@SuppressWarnings({ "unchecked" })
	private AbstractConverter getConverterInstance() {
		return getConverter(userPopulator, JTUserDTO.class.getName());
	}

}
