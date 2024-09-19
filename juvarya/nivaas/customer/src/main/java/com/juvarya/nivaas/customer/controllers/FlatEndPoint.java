package com.juvarya.nivaas.customer.controllers;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

import com.juvarya.nivaas.customer.dto.request.FlatNumbersOnboardDto;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.commonservice.dto.FlatDTO;
import com.juvarya.nivaas.commonservice.dto.Role;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.customer.client.AccessMgmtClient;
import com.juvarya.nivaas.customer.dto.BulkFlatOnboardDto;
import com.juvarya.nivaas.customer.dto.FlatBasicDTO;
import com.juvarya.nivaas.customer.dto.OnboardingRequestDTO;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.customer.model.NotificationModel;
import com.juvarya.nivaas.customer.model.OnboardingRequest;
import com.juvarya.nivaas.customer.model.constants.NotificationType;
import com.juvarya.nivaas.customer.populator.FlatPopulator;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.service.ApartmentUserRoleService;
import com.juvarya.nivaas.customer.service.NivaasFlatService;
import com.juvarya.nivaas.customer.service.NotificationService;
import com.juvarya.nivaas.customer.service.OnboardingRequestService;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.SecurityUtils;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/jtflat")
@Slf4j
public class FlatEndPoint extends JTBaseEndpoint {

	@Autowired
	private NivaasFlatService nivaasFlatService;

	@Autowired
	private FlatPopulator flatPopulator;

	@Autowired
	private NivaasApartmentService apartmentService;

	@Autowired
	private OnboardingRequestService onboardingRequestService;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private AccessMgmtClient accessMgmtClient;

	@Autowired
	private ApartmentUserRoleService apartmentUserRoleService;

	@PostMapping("/bulk/onboard")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity bulkOnboard(@RequestBody @Valid BulkFlatOnboardDto flatDTO) {
		log.info("Bulk onboard request processed: {}", flatDTO);
		onboardingRequestService.bulkAdd(flatDTO);
		return ResponseEntity.ok().body(new MessageResponse("Bulk Flats Onboarded"));
	}

	@PostMapping("/apartment/{apartmentId}/flat/{flatId}/update")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity updateBasicFlatDetails(@PathVariable("apartmentId") Long apartmentId,
			@PathVariable("flatId") Long flatId, @RequestBody @Valid FlatBasicDTO flatDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Processed request to update flat details: apartmentId={}, flatId={}", apartmentId, flatId);

		return nivaasFlatService.updateBasicFlatDetails(apartmentId, flatId, flatDTO);
	}

	@PostMapping("/save")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity save(@Valid @RequestBody FlatDTO flatDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Received request to save flat: {}", flatDTO);

		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		log.debug("LoggedInUser: {}", loggedInUser.getPrimaryContact());
		NivaasApartmentModel nivaasApartmentModel = apartmentService.findById(flatDTO.getApartmentId());

		if (nivaasFlatService.checkFlatExists(flatDTO.getApartmentId(), flatDTO.getFlatNo())) {
			log.warn("Flat number {} already exists in apartment {}", flatDTO.getFlatNo(),
					flatDTO.getApartmentId());
			throw new NivaasCustomerException(ErrorCode.FLAT_ALREADY_EXISTS);
		}

		ApartmentUserRoleModel apartmentUserRoleModel = apartmentUserRoleService
				.findByApartmentModelAndJtCustomer(nivaasApartmentModel, loggedInUser.getId());
		if (Objects.isNull(nivaasApartmentModel)) {
			log.warn("Apartment not found: {}", flatDTO.getApartmentId());
			throw new NivaasCustomerException(ErrorCode.APARTMENT_NOT_FOUND);
		}
		log.debug("NivaasApartmentModel: {}", nivaasApartmentModel);

		if (null != apartmentUserRoleModel && Boolean.TRUE.equals(apartmentUserRoleModel.getApprove())
				&& apartmentUserRoleModel.getRoleName().equals(ERole.ROLE_APARTMENT_ADMIN.name())) {
			log.info("User has apartment admin role and is approved: {}", apartmentUserRoleModel);

			NivaasFlatModel nivaasFlatModel = new NivaasFlatModel();
			nivaasFlatModel.setFacing(flatDTO.getFacing());
			nivaasFlatModel.setFlatNo(flatDTO.getFlatNo());
			nivaasFlatModel.setFloorNo(flatDTO.getFloorNo());
			nivaasFlatModel.setSquareFeet(flatDTO.getSquareFeet());
			nivaasFlatModel.setTotalRooms(flatDTO.getTotalRooms());
			nivaasFlatModel.setAvailableForRent(flatDTO.getIsAvailableForRent());
			nivaasFlatModel.setAvailableForSale(flatDTO.getIsAvailableForSale());
			nivaasFlatModel.setParkingAvailable(flatDTO.getIsParkingAvailable());
			nivaasFlatModel.setApartment(nivaasApartmentModel);

			nivaasFlatModel = nivaasFlatService.save(nivaasFlatModel);
			log.info("Flat saved: {}", nivaasFlatModel);

			NotificationModel notificationModel = new NotificationModel();
			notificationModel.setCreationTime(new Date());
			notificationModel.setFlatModel(nivaasFlatModel);
			notificationModel.setType(NotificationType.APPROVED);
			notificationService.save(notificationModel);
			log.info("Notification created for flat: {}", notificationModel);
			return ResponseEntity.ok().body(new MessageResponse("Flat Created"));
		}
		log.warn("User is not allowed to create flat: {}", loggedInUser.getId());
		throw new NivaasCustomerException(ErrorCode.FLAT_UPDATE_NOT_ALLOWED);
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/flat/details")
	public ResponseEntity getById(@RequestParam Long id)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Received request to get flat details by id: {}", id);
		NivaasFlatModel nivaasFlatModel = nivaasFlatService.findById(id);
		if (null != nivaasFlatModel) {
			log.info("Flat found: {}", nivaasFlatModel);
			FlatDTO flatDTO = (FlatDTO) getConverterInstance().convert(nivaasFlatModel);
			log.debug("Converted flat model to DTO: {}", flatDTO);
			return ResponseEntity.ok().body(flatDTO);
		}
		log.warn("Flat not found with id: {}", id);
		throw new NivaasCustomerException(ErrorCode.FLAT_NOT_FOUND);
	}

	@DeleteMapping("/delete")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN + " " + NivaasConstants.OR + " " + NivaasConstants.ROLE_FLAT_OWNER)
	public ResponseEntity deleteFlat(@Valid @RequestParam Long flatId) {
		log.info("Received request to delete flat with id: {}", flatId);
		NivaasFlatModel nivaasFlatModel = nivaasFlatService.findById(flatId);

		if (null != nivaasFlatModel) {
			log.info("Flat found: {}", nivaasFlatModel);
			nivaasFlatService.removeFlat(nivaasFlatModel);
			log.info("Flat deleted: {}", flatId);
			return ResponseEntity.ok().body(new MessageResponse("Flat Deleted"));
		}
		log.warn("Flat not found with id: {}", flatId);
		throw new NivaasCustomerException(ErrorCode.FLAT_NOT_FOUND);
	}

	@PutMapping("/update")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN + " " + NivaasConstants.OR + " " + NivaasConstants.ROLE_FLAT_OWNER)
	public ResponseEntity updateFlat(@Valid @RequestBody FlatDTO flatDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Received request to update flat details: {}", flatDTO);

		if (null == flatDTO.getId()) {
			return ResponseEntity.ok().body(new MessageResponse("Flat Id Is Required"));
		}

		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		NivaasFlatModel nivaasFlatModel = nivaasFlatService.findById(flatDTO.getId());

		if (Objects.isNull(nivaasFlatModel)) {
			log.warn("Flat not found with id: {}", flatDTO.getId());
			throw new NivaasCustomerException(ErrorCode.FLAT_NOT_FOUND);
		}

		OnboardingRequest onboardingRequest = onboardingRequestService.findByFlatAndAdminApproved(nivaasFlatModel);
		if (Objects.isNull(onboardingRequest)) {
			log.warn("Flat not approved for update: {}", flatDTO.getId());
			throw new NivaasCustomerException(ErrorCode.FLAT_NOT_APPROVED);
		}

		if (loggedInUser.getId().equals(nivaasFlatModel.getOwnerId())) {
			log.info("User authorized to update flat details: {}", loggedInUser.getId());
			if (flatDTO.getIsAvailableForRent()) {
				nivaasFlatModel.setAvailableForRent(flatDTO.getIsAvailableForRent());

				if (null != nivaasFlatModel.getTenantId()) {
					nivaasFlatModel.setTenantId(null);
				}

			}
			if (Objects.nonNull(flatDTO.getIsAvailableForSale()) && flatDTO.getIsAvailableForSale()) {
				nivaasFlatModel.setAvailableForSale(flatDTO.getIsAvailableForSale());
			}

			if (null != flatDTO.getFacing()) {
				nivaasFlatModel.setFacing(flatDTO.getFacing());
			}

			if (null != flatDTO.getFlatNo()) {
				nivaasFlatModel.setFlatNo(flatDTO.getFlatNo());
			}

			if (null != flatDTO.getFloorNo()) {
				nivaasFlatModel.setFloorNo(flatDTO.getFloorNo());
			}

			if (Objects.nonNull(flatDTO.getIsParkingAvailable()) && flatDTO.getIsParkingAvailable()) {
				nivaasFlatModel.setParkingAvailable(flatDTO.getIsParkingAvailable());
			}

			if (null != flatDTO.getSquareFeet()) {
				nivaasFlatModel.setSquareFeet(flatDTO.getSquareFeet());
			}

			if (null != flatDTO.getTotalRooms()) {
				nivaasFlatModel.setTotalRooms(flatDTO.getTotalRooms());
			}
			nivaasFlatService.save(nivaasFlatModel);
			log.info("Flat details updated successfully: {}", flatDTO.getId());
			return ResponseEntity.ok().body(new MessageResponse("Flat Details Updated"));
		}
		log.warn("User not allowed to update flat details: {}", loggedInUser.getId());
		throw new NivaasCustomerException(ErrorCode.FLAT_UPDATE_NOT_ALLOWED);
	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		return getConverter(flatPopulator, FlatDTO.class.getName());

	}

	@PostMapping("/flat/approve")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity approveFlat(@Valid @RequestBody OnboardingRequestDTO onboardingRequestDTO) {
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		log.info("Received request to approve flat with details: {}", onboardingRequestDTO);

		OnboardingRequest flatOnboardRequest = onboardingRequestService.findById(onboardingRequestDTO.getId());

		if (Objects.nonNull(flatOnboardRequest) && null != flatOnboardRequest.getRequestedCustomer()
				&& null != flatOnboardRequest.getFlat()) {

			NivaasFlatModel flatModel = nivaasFlatService.findById(flatOnboardRequest.getFlat().getId());

			OnboardingRequest onboardingRequest = onboardingRequestService.findByFlatAndAdminApproved(flatModel);
			if (Objects.isNull(flatModel)) {
				log.warn("Flat not found with ID: {}", flatOnboardRequest.getFlat().getId());
				throw new NivaasCustomerException(ErrorCode.FLAT_NOT_FOUND);
			} else if (Objects.nonNull(flatModel) && Objects.nonNull(onboardingRequest)) {
				log.info("Flat already approved: {}", flatModel.getId());
				throw new NivaasCustomerException(ErrorCode.FLAT_ALREADY_APPROVED);
			}
			if (loggedInUser.getId().equals(flatModel.getApartment().getCreatedBy())) {
				log.info("User authorized to approve flat: {}", loggedInUser.getId());

				if (onboardingRequestDTO.isAdminApproved()) {

					flatOnboardRequest.setAdminApproved(Boolean.TRUE);
					flatOnboardRequest.setApprovedOn(new Date());
					flatOnboardRequest.setStatus(Boolean.TRUE);
					flatModel.setOwnerId(flatOnboardRequest.getRequestedCustomer());
					UserDTO user = accessMgmtClient.getUserById(flatModel.getOwnerId());
					if (Objects.isNull(user)) {
						user = new UserDTO();
					}
					Role role = accessMgmtClient.getByERole(ERole.ROLE_FLAT_OWNER);
					if (null != role) {
						user.getRoles().add(role);
					}
					user.setCreationTime(new Date());
					accessMgmtClient.saveUser(user);
					nivaasFlatService.save(flatModel);
					onboardingRequestService.save(flatOnboardRequest);

					if (Boolean.TRUE.equals(onboardingRequestDTO.isAdminApproved())) {
						NotificationModel notificationModel = new NotificationModel();
						notificationModel.setCreationTime(new Date());
						notificationModel.setFlatModel(flatModel);
						notificationModel.setType(NotificationType.APPROVED);
						notificationService.save(notificationModel);
					}
					log.info("Flat approved successfully: {}", flatModel.getId());

					return ResponseEntity.ok(new MessageResponse("FLAT APPROVED"));
				}
				log.warn("Flat not approved: {}", flatModel.getId());
				throw new NivaasCustomerException(ErrorCode.FLAT_NOT_APPROVED);
			}
			log.warn("User not allowed to approve flat: {}", loggedInUser.getId());
			throw new NivaasCustomerException(ErrorCode.FLAT_APPROVAL_NOT_ALLOWED);
		}
		log.warn("Request not found or invalid: {}", onboardingRequestDTO.getId());
		throw new NivaasCustomerException(ErrorCode.REQUEST_NOT_FOUND);
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/apartment/flats")
	public ResponseEntity getFlats(@Valid @RequestParam Long apartmentId, @RequestParam int pageNo,
			@RequestParam int pageSize) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<NivaasFlatModel> nivaasFlats = nivaasFlatService.getFlatsByApartment(apartmentId, pageable);

		List<FlatDTO> list = new ArrayList<FlatDTO>();
		if (!CollectionUtils.isEmpty(nivaasFlats.getContent())) {
			for (NivaasFlatModel nivaasFlatModel : nivaasFlats.getContent()) {
				OnboardingRequest onboardingRequest = onboardingRequestService
						.findByFlatAndAdminApproved(nivaasFlatModel);
				if (Objects.nonNull(onboardingRequest)) {
					FlatDTO flatDTO = (FlatDTO) getConverterInstance().convert(nivaasFlatModel);
					list.add(flatDTO);
				}
			}
			Map<String, Object> response = new HashMap<>();
			response.put(NivaasConstants.CURRENT_PAGE, nivaasFlats.getNumber());
			response.put(NivaasConstants.TOTAL_ITEMS, list.size());
			response.put(NivaasConstants.TOTAL_PAGES, nivaasFlats.getTotalPages());
			response.put(NivaasConstants.PAGE_NUM, pageNo);
			response.put(NivaasConstants.PAGE_SIZE, pageSize);
			response.put(NivaasConstants.PROFILES, list);
			log.info("Returning {} flats for apartmentId: {}, page: {}, pageSize: {}", list.size(), apartmentId, pageNo,
					pageSize);

			return new ResponseEntity<>(response, HttpStatus.OK);

		}
		log.warn("No flats found for apartmentId: {}, page: {}, pageSize: {}", apartmentId, pageNo, pageSize);
		return null;
	}

	@GetMapping("/{apartmentId}/flat-owners")
	public ResponseEntity<?> getFlatOwners(@PathVariable Long apartmentId, @RequestParam(defaultValue = "0") int pageNo,
			@RequestParam(defaultValue = "10") int pageSize) {

		log.info("Received request to fetch flat owners for apartment ID: {}", apartmentId);
		Map<String, Object> response = onboardingRequestService.getFlatOwners(apartmentId, pageNo, pageSize);
		if (response == null) {
			log.warn("No flat owners found for apartment ID: {}", apartmentId);
			return ResponseEntity.notFound().build();
		}
		log.info("Returning flat owners for apartment ID: {}", apartmentId);
		return ResponseEntity.ok(response);
	}

	@PostMapping("/without-owners/bulk/onboard")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity flatNumberOnboard(
			@RequestBody @Valid FlatNumbersOnboardDto flatNumbersOnboardDto) {
		log.info("Bulk onboard with out owners request processed: {}", flatNumbersOnboardDto);
		nivaasFlatService.bulkAddWithoutOwners(flatNumbersOnboardDto);
		return ResponseEntity.ok().body(new MessageResponse("With Out Owners Bulk Flats Onboarded"));
	}
	
	@GetMapping("/without-owners/flat/list")
	public ResponseEntity getFlatsWithOutOwners(@RequestParam Long apartmentId, @RequestParam int pageNo, @RequestParam int pageSize) {
		Map<String, Object> response = onboardingRequestService.getFlatsWithOutOwners(apartmentId, pageNo, pageSize);
		return new ResponseEntity<>(response, HttpStatus.OK);
	}
}
