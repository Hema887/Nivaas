package com.juvarya.nivaas.customer.controllers;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

import com.juvarya.nivaas.customer.dto.response.FlatOnboardStatusDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.commonservice.dto.OnboardingRequestDTO;
import com.juvarya.nivaas.commonservice.dto.Role;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.customer.client.AccessMgmtClient;
import com.juvarya.nivaas.customer.dto.response.AccessDto;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.customer.model.OnboardingRequest;
import com.juvarya.nivaas.customer.model.constants.OnboardType;
import com.juvarya.nivaas.customer.model.constants.RelatedType;
import com.juvarya.nivaas.customer.populator.OnboardingRequestPopulator;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.ApartmentUserRoleService;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.service.NivaasFlatService;
import com.juvarya.nivaas.customer.service.OnboardingRequestService;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.SecurityUtils;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@RestController
@RequestMapping("/onboarding")
@Slf4j
public class OnboardingRequestEndpoint extends JTBaseEndpoint {

	@Autowired
	private NivaasApartmentService apartmentService;

	@Autowired
	private OnboardingRequestService onboardingRequestService;

	@Autowired
	private OnboardingRequestPopulator onboardingRequestPopulator;

	@Autowired
	private NivaasFlatService flatService;

	@Autowired
	private ApartmentUserRoleService apartmentUserRoleService;

	@Autowired
	private AccessMgmtClient accessMgmtClient;

	@SuppressWarnings({ "unchecked" })
	@GetMapping("/list")
	public ResponseEntity toBeApproved(@Valid @RequestParam int pageNo, @RequestParam int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Entering Into toBeApproved API");
		@SuppressWarnings("unused")
		UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<OnboardingRequest> onboardingRequest = onboardingRequestService.findByStatus(false, pageable);
		Map<String, Object> response = new HashMap<>();
		response.put(NivaasConstants.CURRENT_PAGE, onboardingRequest.getNumber());
		response.put(NivaasConstants.TOTAL_ITEMS, onboardingRequest.getTotalElements());
		response.put(NivaasConstants.TOTAL_PAGES, onboardingRequest.getTotalPages());
		response.put(NivaasConstants.PAGE_NUM, pageNo);
		response.put(NivaasConstants.PAGE_SIZE, pageSize);
		if (!CollectionUtils.isEmpty(onboardingRequest.getContent())) {
			response.put(NivaasConstants.PROFILES, getConverterInstance().convertAll(onboardingRequest.getContent()));
			log.info("Found {} onboarding requests for approval", onboardingRequest.getContent().size());

			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		log.warn("No onboarding requests found for approval");
		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@SuppressWarnings({ "unchecked" })
	public AbstractConverter getConverterInstance() {
		log.debug("Getting converter instance");
		return getConverter(onboardingRequestPopulator, OnboardingRequestDTO.class.getName());
	}

	@PostMapping("/approveApartment")
	@PreAuthorize(NivaasConstants.ROLE_USER_ADMIN)
	public ResponseEntity approveRequest(@Valid @RequestBody OnboardingRequestDTO onboardingRequestDTO) {

		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		log.info("Approving request with ID: {} by user: {}", onboardingRequestDTO.getId(), loggedInUser.getId());

		OnboardingRequest onboardingRequest = onboardingRequestService.findById(onboardingRequestDTO.getId());

		if (Objects.nonNull(onboardingRequest)) {

			NivaasApartmentModel nivaasApartmentModel = apartmentService
					.findById(onboardingRequest.getApartment().getId());

			if (onboardingRequestDTO.isAdminApproved() && !nivaasApartmentModel.isApprove()) {

				nivaasApartmentModel.setApprove(true);
				onboardingRequest.setAdminApproved(true);
				onboardingRequest.setStatus(true);

				onboardingRequest.setApprovedOn(new Date());

				UserDTO user = accessMgmtClient.getUserById(nivaasApartmentModel.getCreatedBy());
				if (Objects.isNull(user)) {
					user = new UserDTO();
					user.setCreationTime(new Date());
				}

				Role role = accessMgmtClient.getByERole(ERole.ROLE_APARTMENT_ADMIN);
				if (null != role) {
					user.getRoles().add(role);
				}

				accessMgmtClient.saveUser(user);
				apartmentService.saveApartment(nivaasApartmentModel);
				apartmentUserRoleService.onBoardApartmentAdminOrHelper(nivaasApartmentModel,
						nivaasApartmentModel.getCreatedBy(), ERole.ROLE_APARTMENT_ADMIN.name());
				onboardingRequestService.save(onboardingRequest);
				log.info("Apartment with ID {} approved successfully", nivaasApartmentModel.getId());
				return ResponseEntity.ok().body(new MessageResponse("APARTMENT APPROVED"));
			}
		}
		log.warn("User {} is not allowed to approve the request or request with ID {} not found",
				loggedInUser.getPrimaryContact(), onboardingRequestDTO.getId());
		throw new NivaasCustomerException(ErrorCode.REQUEST_NOT_FOUND);

	}

	@PostMapping("/approve/flat/related-user")
	@PreAuthorize(NivaasConstants.ROLE_FLAT_OWNER)
	public ResponseEntity approveTenant(@Valid @RequestBody OnboardingRequestDTO onboardingRequestDTO) {
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		log.info("Received request to approve tenant with details: {}", onboardingRequestDTO);

		OnboardingRequest onboardingRequest = onboardingRequestService.findById(onboardingRequestDTO.getId());

		if (Objects.isNull(onboardingRequest) || null == onboardingRequest.getFlat()) {
			log.warn("Request not found or invalid: {}", onboardingRequestDTO.getId());
			return ResponseEntity.badRequest().body(new MessageResponse("Request Not Found"));
		}
		if (!loggedInUser.getId().equals(onboardingRequest.getFlat().getOwnerId())) {
			log.warn("The approved user is not authorized owner. user: {} flatOwner: {}", loggedInUser.getId(),
					onboardingRequest.getFlat().getOwnerId());
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
					.body(new MessageResponse("Not a valid owner to approve"));
		}
		RelatedType relatedType;
		try {
			relatedType = RelatedType.valueOf(onboardingRequestDTO.getRelatedType());
		} catch (NullPointerException | IllegalArgumentException exception) {
			log.warn("Invalid Onboard request approve type provided in the request");
			throw new NivaasCustomerException(ErrorCode.NOT_SUPPORTED);
		}
		log.info("User authorized to approve tenant for flat: {}", onboardingRequest.getFlat().getId());
		onboardingRequestService.approveFlatRelatedUsers(onboardingRequest, loggedInUser.getId(), relatedType,
				onboardingRequestDTO.getRelatedRequestId());
		log.info("Tenant approved successfully for flat: {}", onboardingRequest.getFlat().getId());
		return ResponseEntity.ok(new MessageResponse("Tenant Approved"));
	}

	/**
	 * To onboard tenant or flat owner
	 */
	@PostMapping("/flat/request")
	public ResponseEntity flatRelatedOnboarding(@Valid @RequestBody OnboardingRequestDTO onboardingRequestDTO) {
		RelatedType relatedType;
		try {
			relatedType = RelatedType.valueOf(onboardingRequestDTO.getRelatedType());
		} catch (NullPointerException | IllegalArgumentException exception) {
			log.warn("Invalid Onboard request type provided in the request");
			throw new NivaasCustomerException(ErrorCode.NOT_SUPPORTED);
		}

		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		NivaasFlatModel flatModel = flatService.findById(onboardingRequestDTO.getFlatId());
		if (Objects.isNull(flatModel)) {
			log.warn("Flat {} not found requested userId: {}", onboardingRequestDTO.getFlatId(), loggedInUser.getId());
			throw new NivaasCustomerException(ErrorCode.FLAT_NOT_FOUND);
		}
		if (loggedInUser.getId().equals(flatModel.getOwnerId())) {
			log.warn("Flat {} owner cannot be tenant/family member, requested userId: {}",
					onboardingRequestDTO.getFlatId(), loggedInUser.getId());
			throw new NivaasCustomerException(ErrorCode.OWNER_INVALID_TENANT_REQUEST);
		}
		onboardingRequestService.flatRelatedOnboarding(onboardingRequestDTO, flatModel, relatedType);
		return ResponseEntity.ok(new MessageResponse(relatedType + " OnBoarding Request Sent"));
	}

	@PostMapping("/transferOwner/Request")
	public ResponseEntity ownerTransferRequest(@Valid @RequestParam Long flatId) {
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		NivaasFlatModel flatModel = flatService.findById(flatId);

		if (Objects.isNull(flatModel)) {
			log.warn("Flat with ID {} not found", flatId);
			return ResponseEntity.ok(new MessageResponse("Flat Not Found"));
		}

		if (flatModel.isAvailableForSale() && Objects.nonNull(flatModel.getOwnerId())) {
			OnboardingRequest onboardingRequest = new OnboardingRequest();
			onboardingRequest.setFlat(flatModel);
			onboardingRequest.setOnboardType(OnboardType.FLAT);
			onboardingRequest.setCreationTime(new Date());
			onboardingRequest.setApartment(flatModel.getApartment());
			onboardingRequest.setRequestedCustomer(loggedInUser.getId());
			onboardingRequestService.save(onboardingRequest);

			log.info("Owner transfer request for Flat ID {} sent by user {}", flatId, loggedInUser.getPrimaryContact());
			return ResponseEntity.ok(new MessageResponse("Flat Transfer OnBoarding Request Sent"));
		}
		log.warn("Flat with ID {} is not available for sale or does not have an owner", flatId);
		return ResponseEntity.badRequest().body(new MessageResponse("Flat Not For Sale"));
	}

	@PostMapping("/transferOwner/Approve")
	@PreAuthorize(NivaasConstants.ROLE_FLAT_OWNER)
	public ResponseEntity ownerTransferApprove(@Valid @RequestParam Long onboardingRequestId,
			@RequestParam Boolean status) {
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		log.info("Received request to approve owner transfer: onboardingRequestId={}, status={}", onboardingRequestId,
				status);

		log.debug("Current user: {}", loggedInUser.getId());

		OnboardingRequest flatOnboardRequest = onboardingRequestService.findById(onboardingRequestId);
		log.debug("OnboardingRequest: {}", flatOnboardRequest);

		NivaasFlatModel flatModel = flatService.findById(flatOnboardRequest.getFlat().getId());
		log.debug("NivaasFlatModel: {}", flatModel);

		if (flatModel.getOwnerId().equals(loggedInUser.getId()) && status) {
			flatModel.setOwnerId(flatOnboardRequest.getRequestedCustomer());
			flatModel.setAvailableForSale(false);
			flatService.save(flatModel);
			log.info("Ownership transferred successfully: flatId={}, newOwner={}", flatModel.getId(),
					flatModel.getOwnerId());

			return ResponseEntity.ok().body(new MessageResponse("OwnerShip Transferred"));
		}
		log.warn("Ownership transfer failed: user={} is not allowed to transfer ownership of flatId={}",
				loggedInUser.getId(), flatModel.getId());

		return ResponseEntity.badRequest().body(new MessageResponse("You Are Not Allowed To Transfer"));

	}

	@GetMapping("/requests")
	public ResponseEntity getOnboardRequests()
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		AccessDto accessDto = onboardingRequestService.getOnboardRequests();
		return ResponseEntity.ok().body(accessDto);
	}

	@GetMapping("/flat/list/{apartmentId}")
	public ResponseEntity getApartmentByFlatRequests(@PathVariable("apartmentId") Long apartmentId) {
		List<FlatOnboardStatusDto> flatOnboardStatusDtos = onboardingRequestService.getApartmentByflatRequests(apartmentId);
		return ResponseEntity.ok(flatOnboardStatusDtos);
	}
	
	@PostMapping("/flat_owner/request")
	public ResponseEntity flatOwnerRequest(@Valid @RequestBody OnboardingRequestDTO onboardingRequestDTO) {
		OnboardType onboardType;
		 try {
			 onboardType = OnboardType.valueOf(onboardingRequestDTO.getType());
		 } catch (NullPointerException | IllegalArgumentException exception) {
				log.warn("Invalid Onboard request type provided in the request");
				throw new NivaasCustomerException(ErrorCode.NOT_SUPPORTED);
			}
		 
		 UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		 NivaasFlatModel flatModel = flatService.findById(onboardingRequestDTO.getFlatId());
		 if (Objects.isNull(flatModel)) {
				log.warn("Flat {} not found requested userId: {}", onboardingRequestDTO.getFlatId(), loggedInUser.getId());
				throw new NivaasCustomerException(ErrorCode.FLAT_NOT_FOUND);
			}
		 
		 if (null != flatModel.getOwnerId()) {
				log.warn("Flat {} owner already available, requested userId: {}",
						onboardingRequestDTO.getFlatId(), loggedInUser.getId());
				throw new NivaasCustomerException(ErrorCode.FLAT_OWNER_AVAILABLE);
			}
		 onboardingRequestService.flatOwnerOnboardingRequest(onboardingRequestDTO, flatModel, onboardType);
		 return ResponseEntity.ok(new MessageResponse(onboardType + " OnBoarding Request Sent"));
	}
	
	@PostMapping("/approve/falt_owner")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity ApproveFlatOnboardingRequest(@Valid @RequestBody OnboardingRequestDTO onboardingRequestDTO) {
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		OnboardingRequest onboardingRequest = onboardingRequestService.findById(onboardingRequestDTO.getId());

		if (Objects.isNull(onboardingRequest) || null == onboardingRequest) {
			throw new NivaasCustomerException(ErrorCode.FLAT_ONBOARD_NOT_ALLOWED);
		}

		if (!loggedInUser.getId().equals(onboardingRequest.getApartment().getCreatedBy())) {
			log.warn("The approved user is not authorized owner. user: {} ", loggedInUser.getId());
			return ResponseEntity.status(HttpStatus.NOT_ACCEPTABLE)
					.body(new MessageResponse("Not a valid owner to approve"));
		}

		OnboardType onboardType;

		try {
			onboardType = OnboardType.valueOf(onboardingRequestDTO.getType());
		} catch (NullPointerException | IllegalArgumentException exception) {
			log.warn("Invalid Onboard request approve type provided in the request");
			throw new NivaasCustomerException(ErrorCode.NOT_SUPPORTED);
		}
		log.info("User authorized to approve owner for flat: {}", onboardingRequest.getFlat().getId());
		log.info("owner approved successfully for flat: {}", onboardingRequest.getFlat().getId());
		onboardingRequestService.approveFlatOwner(onboardingRequest, loggedInUser.getId(), onboardingRequest.getFlat(),
				onboardType);
		return ResponseEntity.ok(new MessageResponse("Flat Approved"));
	}

}
