package com.juvarya.nivaas.customer.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.commonservice.dto.BasicOnboardUserDTO;
import com.juvarya.nivaas.commonservice.dto.FlatDTO;
import com.juvarya.nivaas.commonservice.dto.JTUserDTO;
import com.juvarya.nivaas.commonservice.dto.LoggedInUser;
import com.juvarya.nivaas.commonservice.dto.OnboardingRequestDTO;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.customer.client.AccessMgmtClient;
import com.juvarya.nivaas.customer.dto.BulkFlatOnboardDto;
import com.juvarya.nivaas.customer.dto.response.AccessDto;
import com.juvarya.nivaas.customer.dto.response.ApartmentAccessDto;
import com.juvarya.nivaas.customer.dto.response.CurrentApartmentDto;
import com.juvarya.nivaas.customer.dto.response.CurrentUserDto;
import com.juvarya.nivaas.customer.dto.response.FlatAccessDto;
import com.juvarya.nivaas.customer.dto.response.FlatOnboardStatusDto;
import com.juvarya.nivaas.customer.firebase.listeners.NotificationPublisher;
import com.juvarya.nivaas.customer.model.ApartmentAndFlatRelatedUsersModel;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.customer.model.NotificationModel;
import com.juvarya.nivaas.customer.model.OnboardingRequest;
import com.juvarya.nivaas.customer.model.constants.NotificationType;
import com.juvarya.nivaas.customer.model.constants.OnboardType;
import com.juvarya.nivaas.customer.model.constants.RelatedType;
import com.juvarya.nivaas.customer.populator.OnboardingRequestPopulator;
import com.juvarya.nivaas.customer.proxy.AccessMgmtClientProxy;
import com.juvarya.nivaas.customer.repository.ApartmentAndFlatRelatedUsersModelRepository;
import com.juvarya.nivaas.customer.repository.ApartmentUserRoleRepository;
import com.juvarya.nivaas.customer.repository.OnboardingRequestRepository;
import com.juvarya.nivaas.customer.service.CurrentApartmentService;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.service.NivaasFlatService;
import com.juvarya.nivaas.customer.service.NotificationService;
import com.juvarya.nivaas.customer.service.OnboardingRequestService;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.SecurityUtils;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@Service
@Slf4j
public class OnboardingRequestServiceImpl extends JTBaseEndpoint implements OnboardingRequestService {

	@Autowired
	private OnboardingRequestRepository onboardingRequestRepository;

	@Autowired
	private OnboardingRequestPopulator onboardingRequestPopulator;

	@Autowired
	private AccessMgmtClientProxy accessMgmtClientProxy;

	@Autowired
	private AccessMgmtClient accessMgmtClient;

	@Autowired
	private ApartmentAndFlatRelatedUsersModelRepository relatedUsersModelRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private NotificationPublisher notificationPublisher;

	@Autowired
	private NivaasFlatService flatService;

	@Autowired
	private NivaasApartmentService apartmentService;

	@Autowired
	private ApartmentUserRoleRepository apartmentUserRoleRepository;

	@Autowired
	private CurrentApartmentService currentApartmentService;

	@Transactional
	@Override
	public void bulkAdd(final BulkFlatOnboardDto flatOnboardDto) {
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		log.info("Starting bulk add for flats: {} LoggedInUser: {}", flatOnboardDto, loggedInUser.getId());

		NivaasApartmentModel jtApartmentModel = apartmentService.findById(flatOnboardDto.getApartmentId());
		if (null == jtApartmentModel) {
			log.warn("Apartment not found: {}", flatOnboardDto.getApartmentId());
			throw new NivaasCustomerException(ErrorCode.APARTMENT_NOT_FOUND);
		}
		log.debug("NivaasApartmentModel: {}", jtApartmentModel);

		int totalFlats = jtApartmentModel.getTotalFlats();
		List<NivaasFlatModel> flatModels = flatService.getAllFlatsByApartment(jtApartmentModel.getId());
		int flats = flatModels.size();

		ApartmentUserRoleModel apartmentUserRoleModel = apartmentUserRoleRepository
				.findByApartmentModelAndCustomerId(jtApartmentModel, loggedInUser.getId());
		if (null == apartmentUserRoleModel) {
			log.warn("User is not allowed to onboard flats: {}", loggedInUser.getId());
			throw new NivaasCustomerException(ErrorCode.FLAT_ONBOARD_NOT_ALLOWED);
		}
		int onboardFlats = flatOnboardDto.getFlats().size();
		if (flats + onboardFlats > totalFlats) {
			throw new NivaasCustomerException(ErrorCode.FLAT_LIMIT, ErrorCode.FLAT_LIMIT.formatMessage(totalFlats));
		}
		log.info("User has apartment role: {}", apartmentUserRoleModel);
		List<NivaasFlatModel> flatModelList = new ArrayList<>();
		List<OnboardingRequest> onboardingRequests = new ArrayList<>();
		List<NotificationModel> notificationModels = new ArrayList<>();
		flatOnboardDto.getFlats().stream().filter(
				flatBasicDTO -> !flatService.checkFlatExists(flatOnboardDto.getApartmentId(), flatBasicDTO.getFlatNo()))
				.forEach(flatBasicDTO -> {
					NivaasFlatModel flatModel = new NivaasFlatModel();
					LoggedInUser owner = accessMgmtClient.getByPrimaryContact(flatBasicDTO.getOwnerPhoneNo());
					Long flatOwnerId;
					if (null != owner) {
						accessMgmtClient.addRole(owner.getId(), ERole.ROLE_FLAT_OWNER);
						flatModel.setOwnerId(owner.getId());
						flatOwnerId = owner.getId();
						log.debug("Owner found and role added: {}", owner.getId());
					} else {
						BasicOnboardUserDTO basicOnboardUserDTO = BasicOnboardUserDTO.builder()
								.primaryContact(flatBasicDTO.getOwnerPhoneNo()).fullName(flatBasicDTO.getOwnerName())
								.userRoles(Set.of(ERole.ROLE_USER, ERole.ROLE_FLAT_OWNER)).build();
						flatOwnerId = accessMgmtClient.onBoardUser(basicOnboardUserDTO);
						flatModel.setOwnerId(flatOwnerId);
						log.debug("New owner onboarded: {}", flatOwnerId);
					}
					// By-default setting flat is available for rent
					flatModel.setAvailableForRent(true);
					flatModel.setFlatNo(flatBasicDTO.getFlatNo());
					flatModel.setApartment(jtApartmentModel);
					// LoggedIn user sets as requested user because he raised bulk add
					onboardingRequests.add(buildOnboardRequest(jtApartmentModel, flatModel, flatOwnerId));

					flatModelList.add(flatModel);
					NotificationModel notificationModel = getNotificationModel(jtApartmentModel, flatModel,
							flatOwnerId);
					notificationModels.add(notificationModel);
					currentApartmentService.setCurrentApartmentIfNotExists(flatOwnerId, jtApartmentModel.getId());
				});
		flatService.saveAll(flatModelList);
		log.info("Saved flat models: {}", flatModelList);
		bulkOnBoardFlat(onboardingRequests);
		log.info("Bulk onboarded flats: {}", onboardingRequests);
		notificationService.saveAll(notificationModels);
		log.info("Saved notifications: {}", notificationModels);

		notificationModels.forEach(notification -> notificationPublisher.sendNotification(
				notification.getNivaasApartmentModel().getId(), notification.getUserId(), false, false,
				notification.getFlatModel().getId(), true, true, null, null, 0, false, null));
		log.info("Notifications sent");
	}

	@Override
	public Map<String, Object> getFlatOwners(Long apartmentId, int pageNo, int pageSize) {
		log.info("Fetching flat owners for apartment ID: {}", apartmentId);
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<NivaasFlatModel> flats = flatService.getFlatsByApartment(apartmentId, pageable);

		if (flats == null || CollectionUtils.isEmpty(flats.getContent())) {
			log.warn("No flats found for apartment ID: {}", apartmentId);
			return null;
		}

		List<JTUserDTO> users = new ArrayList<>();

		Map<String, Object> response = new HashMap<>();
		for (NivaasFlatModel flatModel : flats) {
			OnboardingRequest onboardingRequest = findByFlatAndAdminApproved(flatModel);

			if (null != flatModel.getOwnerId() && null != onboardingRequest
					&& onboardingRequest.getOnboardType().equals(OnboardType.FLAT)
					&& flatModel.getOwnerId().equals(onboardingRequest.getRequestedCustomer())) {
				UserDTO user = accessMgmtClient.getUserById(flatModel.getOwnerId());
				JTUserDTO jtUserDTO = new JTUserDTO();
				jtUserDTO.setId(flatModel.getOwnerId());
				jtUserDTO.setFullName(user.getFullName());
				jtUserDTO.setPrimaryContact(user.getPrimaryContact());

				users.add(jtUserDTO);
			}
		}

		response.put(NivaasConstants.CURRENT_PAGE, flats.getNumber());
		response.put(NivaasConstants.TOTAL_ITEMS, users.size());
		response.put(NivaasConstants.TOTAL_PAGES, flats.getTotalPages());
		response.put(NivaasConstants.PAGE_NUM, pageNo);
		response.put(NivaasConstants.PAGE_SIZE, pageSize);
		response.put(NivaasConstants.PROFILES, users);

		log.info("Returning {} flat owners for apartment ID: {}", users.size(), apartmentId);
		return response;
	}

	@Transactional
	public OnboardingRequest save(OnboardingRequest jtonboardingRequest) {
		return onboardingRequestRepository.save(jtonboardingRequest);
	}

	public void bulkOnBoardFlat(final List<OnboardingRequest> onboardingRequests) {
		onboardingRequestRepository.saveAll(onboardingRequests);
	}

	public OnboardingRequest findById(Long id) {
		Optional<OnboardingRequest> onbOptional = onboardingRequestRepository.findById(id);
		return onbOptional.orElse(null);
	}

	@Override
	public Page<OnboardingRequest> findByStatus(boolean status, Pageable pageble) {
		return onboardingRequestRepository.findByStatus(status, pageble);
	}

	@Override
	public Page<OnboardingRequest> findByFlat(NivaasFlatModel nivaasFlatModel, Pageable pageable) {
		return onboardingRequestRepository.findByFlat(nivaasFlatModel, pageable);
	}

	@Override
	public OnboardingRequest findByFlatAndAdminApproved(NivaasFlatModel nivaasFlatModel) {
		return onboardingRequestRepository.findByFlatAndAdminApproved(nivaasFlatModel, Boolean.TRUE);
	}

	@Override
	public List<OnboardingRequest> findByRequestCustomer(Long userId) {
		return onboardingRequestRepository.findByRequestedCustomer(userId);
	}

	@Override
	public CurrentApartmentDto findByUserAndApartmentId(final LoggedInUser loggedInUser, final Long apartmentId) {
		List<OnboardingRequest> requests = onboardingRequestRepository.findByUserAndApartmentId(loggedInUser.getId(),
				apartmentId);
		return buildCurrentApartmentAccessDto(loggedInUser, requests);
	}

	@Override
	public boolean isValidApartmentUserMap(final Long userId, final Long apartmentId) {
		return onboardingRequestRepository.existsByUserAndApartmentId(userId, apartmentId);
	}

	@Override
	public void onBoardApartmentAdmin(NivaasApartmentModel nivaasApartmentModel, Long customerId) {
		OnboardingRequest onboardingRequest = new OnboardingRequest();

		onboardingRequest.setApartment(nivaasApartmentModel);
		onboardingRequest.setAdminApproved(false);
		onboardingRequest.setStatus(false);
		onboardingRequest.setCreationTime(new Date());
		onboardingRequest.setModificationTime(new Date());
		onboardingRequest.setOnboardType(OnboardType.APARTMENT);
		onboardingRequest.setRequestedCustomer(customerId);
		save(onboardingRequest);
	}

	@Override
	public void onBoardCoAdmin(final Long apartmentId, final Long userId) {
		List<OnboardingRequest> onboardingRequests = onboardingRequestRepository
				.findByApartmentAndAdminApprovedAndOnboardType(apartmentId, OnboardType.APARTMENT);
		if (!CollectionUtils.isEmpty(onboardingRequests) && onboardingRequests.size() == 1) {
			buildAndSaveOnBoardRelatedUsers(onboardingRequests.get(0), userId, RelatedType.CO_ADMIN, true);
		} else {
			log.warn("Invalid onboard coAdmin request for apartment {} coAdmin user {}", apartmentId, userId);
		}

	}

	@Override
	@Transactional
	public void flatRelatedOnboarding(final OnboardingRequestDTO onboardingRequestDTO, final NivaasFlatModel flatModel,
			final RelatedType relatedType) {
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		OnboardingRequest onboardingRequest = findByFlatAndAdminApproved(flatModel);
		if (onboardingRequest == null) {
			log.warn("Did not find valid onboarding request for {}", flatModel.getId());
			throw new NivaasCustomerException(ErrorCode.NOT_VALID);
		}

		boolean isRequestAlreadyExists = onboardingRequest.getRelatedUsers() != null
				&& onboardingRequest.getRelatedUsers().stream().anyMatch(
						o -> relatedType.equals(o.getRelatedType()) && o.getUserId().equals(loggedInUser.getId()));
		if (isRequestAlreadyExists) {
			log.warn("Request already exists for user {} flat {}", loggedInUser.getId(), flatModel.getId());
			throw new NivaasCustomerException(ErrorCode.DUPLICATE);
		}
		switch (relatedType) {
		case TENANT:
			if (Boolean.FALSE.equals(flatModel.isAvailableForRent())) {
				throw new NivaasCustomerException(ErrorCode.FLAT_NOT_AVAILABLE_FOR_RENT);
			}
			log.info("Sending tenant onboarding request for flat ID: {}", flatModel.getId());
			buildAndOnboardFlatRelatedUser(flatModel, loggedInUser.getId(), RelatedType.TENANT,
					NotificationType.TENANT_ONBOARD);
			break;
		case FLAT_OWNER_FAMILY_MEMBER:
			log.info("Sending family member onboarding request for flat ID: {}", flatModel.getId());
			buildAndOnboardFlatRelatedUser(flatModel, loggedInUser.getId(), RelatedType.FLAT_OWNER_FAMILY_MEMBER,
					NotificationType.FLAT_OWNER_FAMILY_MEMBER);
			break;
		default:
			throw new NivaasCustomerException(ErrorCode.NOT_SUPPORTED);
		}
	}

	private void buildAndOnboardFlatRelatedUser(final NivaasFlatModel flatModel, final Long userId,
			final RelatedType relatedType, final NotificationType notificationType) {
		List<OnboardingRequest> onboardingRequests = onboardingRequestRepository
				.findByFlatAndAdminApprovedAndOnboardType(flatModel.getId(), OnboardType.FLAT);
		if (!CollectionUtils.isEmpty(onboardingRequests) && onboardingRequests.size() == 1) {
			buildAndSaveOnBoardRelatedUsers(onboardingRequests.get(0), userId, relatedType, false);
			buildAndSendTenantNotification(flatModel, userId, notificationType);
			currentApartmentService.setCurrentApartmentIfNotExists(userId, flatModel.getApartment().getId());
		} else {
			log.warn("Invalid onboard tenant request for flat {} tenant user {}", flatModel.getId(), userId);
		}
	}

	/**
	 *
	 * @param onboardingRequest
	 * @param currentUserId current user Id should not be tenant or family member
	 * @param relatedType
	 * @param relatedRequestId related request Id, this helps in identifying the exact related request
	 */
	@Override
	public void approveFlatRelatedUsers(final OnboardingRequest onboardingRequest, final Long currentUserId,
			final RelatedType relatedType, final Long relatedRequestId) {
		if(relatedType.equals(RelatedType.TENANT)) {
		ApartmentAndFlatRelatedUsersModel apartmentAndFlatRelatedUsersModel = onboardingRequest.getRelatedUsers()
				.stream().filter(o -> RelatedType.TENANT.equals(o.getRelatedType()) && !o.getUserId().equals(currentUserId)
						&& o.getId().equals(relatedRequestId))
				.findFirst().orElseThrow(() -> new NivaasCustomerException(ErrorCode.NOT_FOUND));
		apartmentAndFlatRelatedUsersModel.setRelatedUserApproved(true);
		Long relatedUserId = apartmentAndFlatRelatedUsersModel.getUserId();
		accessMgmtClient.addRole(relatedUserId, ERole.ROLE_FLAT_TENANT);
		relatedUsersModelRepository.save(apartmentAndFlatRelatedUsersModel);
		} else 
			if(relatedType.equals(RelatedType.FLAT_OWNER_FAMILY_MEMBER)) {
			ApartmentAndFlatRelatedUsersModel apartmentAndFlatRelatedUsersModel = onboardingRequest.getRelatedUsers()
					.stream().filter(o -> RelatedType.FLAT_OWNER_FAMILY_MEMBER.equals(o.getRelatedType()) && !o.getUserId().equals(currentUserId)
							&& o.getId().equals(relatedRequestId))
					.findAny().orElseThrow(() -> new NivaasCustomerException(ErrorCode.NOT_FOUND));
			apartmentAndFlatRelatedUsersModel.setRelatedUserApproved(true);
			relatedUsersModelRepository.save(apartmentAndFlatRelatedUsersModel);
		}
		// TODO: need to understand the requirement to have tenantId in flat model
		// Also, not updating available for rent to false because to support multiple
		// tenants

		buildAndSendTenantNotification(onboardingRequest.getFlat(), currentUserId, NotificationType.TENANT_APPROVAL);
	}

	@Override
	public AccessDto getOnboardRequests() {
		LoggedInUser loggedInUser = accessMgmtClientProxy.getCurrentCustomer();
		if (loggedInUser != null) {
			List<OnboardingRequest> requests = findByRequestCustomer(loggedInUser.getId());
			return buildLoggedInUserWithOnboardRequest(loggedInUser, requests);
		}
		return null;
	}

	private void buildAndSaveOnBoardRelatedUsers(final OnboardingRequest existingOnboardRequest,
			final Long relatedUserId, final RelatedType relatedType, final boolean relatedUserApproved) {
		ApartmentAndFlatRelatedUsersModel apartmentAndFlatRelatedUsersModel = new ApartmentAndFlatRelatedUsersModel();
		apartmentAndFlatRelatedUsersModel.setUserId(relatedUserId);
		apartmentAndFlatRelatedUsersModel.setOnboardingRequestId(existingOnboardRequest.getId());
		apartmentAndFlatRelatedUsersModel.setRelatedType(relatedType);
		apartmentAndFlatRelatedUsersModel.setRelatedUserApproved(relatedUserApproved);
		apartmentAndFlatRelatedUsersModel = relatedUsersModelRepository.save(apartmentAndFlatRelatedUsersModel);
		List<ApartmentAndFlatRelatedUsersModel> relatedUsersModels = existingOnboardRequest.getRelatedUsers();
		relatedUsersModels.add(apartmentAndFlatRelatedUsersModel);
		existingOnboardRequest.setRelatedUsers(relatedUsersModels);
		onboardingRequestRepository.save(existingOnboardRequest);
	}

	private static NotificationModel getNotificationModel(NivaasApartmentModel jtApartmentModel,
			NivaasFlatModel flatModel, Long flatOwnerId) {
		NotificationModel notificationModel = new NotificationModel();
		notificationModel.setCreationTime(new Date());
		notificationModel.setNivaasApartmentModel(jtApartmentModel);
		notificationModel.setFlatModel(flatModel);
		notificationModel.setUserId(flatOwnerId);
		notificationModel.setType(NotificationType.FLAT_APPROVED);
		notificationModel.setMessage(jtApartmentModel.getName() + " " + flatModel.getFlatNo() + " " + flatOwnerId + " "
				+ ",NIVAAS Admin Approved Your Flat");
		return notificationModel;
	}

	private AccessDto buildLoggedInUserWithOnboardRequest(final LoggedInUser loggedInUser,
			final List<OnboardingRequest> requests) {
		final CurrentUserDto user = CurrentUserDto.buildCurrentUserDto(loggedInUser);
		List<ApartmentAccessDto> apartmentAccessDto = new ArrayList<>();
		if (!CollectionUtils.isEmpty(requests)) {
			Map<NivaasApartmentModel, List<OnboardingRequest>> groupedByApartment = requests.stream()
					.collect(Collectors.groupingBy(OnboardingRequest::getApartment));
			apartmentAccessDto = groupedByApartment.values().stream()
					.map(onboardingRequests -> buildApartmentAccessDto(onboardingRequests, loggedInUser.getId()))
					.collect(Collectors.toList());
		}
		return AccessDto.builder().user(user).apartments(apartmentAccessDto).build();
	}

	private CurrentApartmentDto buildCurrentApartmentAccessDto(final LoggedInUser loggedInUser,
															   final List<OnboardingRequest> requests) {
		final CurrentUserDto user = CurrentUserDto.buildCurrentUserDto(loggedInUser);
		if (CollectionUtils.isEmpty(requests) || requests.stream().map(OnboardingRequest::getApartment).distinct().count() != 1) {
			return CurrentApartmentDto.builder()
					.user(user)
					.build();
		}
		return CurrentApartmentDto.builder()
				.user(user)
				.currentApartment(buildApartmentAccessDto(requests, loggedInUser.getId()))
				.build();
	}

	private ApartmentAccessDto buildApartmentAccessDto(final List<OnboardingRequest> requests, final Long userId) {
		List<FlatAccessDto> flatAccesslist = requests.stream()
				.filter(onboardingRequest -> OnboardType.FLAT.equals(onboardingRequest.getOnboardType()))
				.flatMap(request -> FlatAccessDto.buildAccessFlatsDto(request, userId).stream())
				.collect(Collectors.toList());
		List<OnboardingRequest> apartmentTypeRequests = requests.stream()
				.filter(onboardingRequest -> OnboardType.APARTMENT.equals(onboardingRequest.getOnboardType()))
				.collect(Collectors.toList());
		if (CollectionUtils.isEmpty(apartmentTypeRequests)) {
			NivaasApartmentModel apartmentModel = requests.get(0).getApartment();
			return ApartmentAccessDto.buildAccessApartmentDtoOnlyFlatRelatedUsers(apartmentModel, flatAccesslist);
		} else {
			NivaasApartmentModel apartmentModel = apartmentTypeRequests.get(0).getApartment();
			return ApartmentAccessDto.buildAccessApartmentDto(apartmentModel, flatAccesslist,
					apartmentTypeRequests, userId);
		}
	}

	private void buildAndSendTenantNotification(final NivaasFlatModel flatModel, final Long userId,
			final NotificationType notificationType) {
		NotificationModel notificationModel = new NotificationModel();
		notificationModel.setCreationTime(new Date());
		notificationModel.setFlatModel(flatModel);
		notificationModel.setNivaasApartmentModel(flatModel.getApartment());
		notificationModel.setType(notificationType);
		notificationModel.setUserId(userId);
		
		if (notificationType.equals(NotificationType.FLAT_ONBOARD)) {
			notificationModel.setMessage("Flat Owner OnBoarding Request Sent");
		} else if (notificationType.equals(NotificationType.FLAT_APPROVED)) {
			notificationModel.setMessage("Flat OnBoarding Request Approved");
		} else if (notificationType.equals(NotificationType.TENANT_APPROVAL)) {
			notificationModel.setMessage("Tenant OnBoarding Request Approved");
		}else {
			notificationModel.setMessage("Tenant OnBoarding Request Sent");
		}

		notificationService.save(notificationModel);
		notificationPublisher.sendNotification(notificationModel.getNivaasApartmentModel().getId(), null, false, false,
				notificationModel.getFlatModel().getId(), true, false, null, null, 0, true,
				notificationModel.getUserId());

	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		return getConverter(onboardingRequestPopulator, OnboardingRequestDTO.class.getName());
	}

	private static OnboardingRequest buildOnboardRequest(final NivaasApartmentModel nivaasApartmentModel,
			final NivaasFlatModel flatModel, final Long userId) {
		OnboardingRequest onboardingRequest = new OnboardingRequest();
		onboardingRequest.setApartment(nivaasApartmentModel);
		onboardingRequest.setFlat(flatModel);
		onboardingRequest.setAdminApproved(Boolean.TRUE);
		onboardingRequest.setStatus(Boolean.TRUE);
		onboardingRequest.setOnboardType(OnboardType.FLAT);
		onboardingRequest.setCreationTime(new Date());
		onboardingRequest.setApprovedOn(new Date());
		onboardingRequest.setModificationTime(new Date());
		onboardingRequest.setRequestedCustomer(userId);
		return onboardingRequest;
	}

	@Override
	public List<FlatOnboardStatusDto> getApartmentByflatRequests(final Long apartmentId) {
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		log.info("On Start of getting flat details for LoggedInUser: {}", loggedInUser.getId());

		NivaasApartmentModel apartmentModel = apartmentService.findById(apartmentId);
		if (Objects.isNull(apartmentModel)) {
			log.warn("Apartment not found: {}", apartmentId);
			throw new NivaasCustomerException(ErrorCode.APARTMENT_NOT_FOUND);
		}
		
		List<OnboardingRequest> flatOnboardRequests = onboardingRequestRepository.getAllFlatRequestsByApartmentId(apartmentId);
		if(CollectionUtils.isEmpty(flatOnboardRequests)) {
			log.warn("Flats not found: {}", apartmentId);
			throw new NivaasCustomerException(ErrorCode.FLAT_NOT_FOUND);
		}
		
		log.debug("NivaasApartmentModel: {}", apartmentModel);
		ApartmentUserRoleModel apartmentUserRoleModel = apartmentUserRoleRepository
				.findByApartmentModelAndCustomerId(apartmentModel, loggedInUser.getId());
		// Collect all user IDs from both related users and the requestedCustomer
		List<Long> userIds = flatOnboardRequests.stream()
				.flatMap(onboardingRequest -> {
					Stream<Long> relatedUserIds = onboardingRequest.getRelatedUsers().stream()
							.map(ApartmentAndFlatRelatedUsersModel::getUserId);

					return Stream.concat(Stream.of(onboardingRequest.getRequestedCustomer()), relatedUserIds);
				})
				.distinct()
				.collect(Collectors.toList());

		// Fetch all user details in a single call
		Map<Long, UserDTO> userDetailsMap = accessMgmtClient.getUserDetailsByIds(userIds).stream()
				.collect(Collectors.toMap(UserDTO::getId, userDetailsDto -> userDetailsDto));

		//Fetch all the flats for apartment admin
		if (null != apartmentUserRoleModel) {
			// Map to FlatOnboardStatusDto
			return flatOnboardRequests.stream()
					.flatMap(onboardingRequest -> {
						List<FlatOnboardStatusDto> dtos = new ArrayList<>();

						// Add the flat owner (requestedCustomer)
						UserDTO flatOwnerDetails = userDetailsMap.get(onboardingRequest.getRequestedCustomer());
						if (flatOwnerDetails != null) {
							dtos.add(FlatOnboardStatusDto.buildFlatOwnerStatusDto(onboardingRequest, flatOwnerDetails, "FLAT_OWNER",
									onboardingRequest.isAdminApproved(), null));
						}
						// Add all related users
						dtos.addAll(onboardingRequest.getRelatedUsers().stream()
										.filter(relatedUsers -> !RelatedType.CO_ADMIN.equals(relatedUsers.getRelatedType()))
								.map(relatedUser -> {
									UserDTO userDetails = userDetailsMap.get(relatedUser.getUserId());
									return FlatOnboardStatusDto.buildFlatOwnerStatusDto(onboardingRequest, userDetails,
											relatedUser.getRelatedType().name(), relatedUser.isRelatedUserApproved(), relatedUser.getId());
								})
								.collect(Collectors.toList()));

						return dtos.stream();
					})
					.collect(Collectors.toList());
		}
		return getFlatsForNonAdminUsers(loggedInUser.getId(), flatOnboardRequests, userDetailsMap);
	}

	private List<FlatOnboardStatusDto> getFlatsForNonAdminUsers(final Long userId, final List<OnboardingRequest> flatOnboardRequests,
																final Map<Long, UserDTO> userDetailsMap) {
		return flatOnboardRequests.stream()
				.flatMap(onboardingRequest -> {
					List<FlatOnboardStatusDto> dtos = new ArrayList<>();
					// Check if the user is the flat owner (requestedCustomer)
					if (onboardingRequest.getRequestedCustomer().equals(userId)) {
						// Add the flat owner details
						UserDTO flatOwnerDetails = userDetailsMap.get(userId);
						if (flatOwnerDetails != null) {
							dtos.add(FlatOnboardStatusDto.buildFlatOwnerStatusDto(onboardingRequest, flatOwnerDetails,
									"FLAT_OWNER", onboardingRequest.isAdminApproved(), null));
							// Add all related users for this flat
							dtos.addAll(onboardingRequest.getRelatedUsers().stream()
									.filter(relatedUsers -> !RelatedType.CO_ADMIN.equals(relatedUsers.getRelatedType()))
									.map(relatedUser -> {
										UserDTO userDetails = userDetailsMap.get(relatedUser.getUserId());
										return FlatOnboardStatusDto.buildFlatOwnerStatusDto(onboardingRequest, userDetails,
												relatedUser.getRelatedType().name(), relatedUser.isRelatedUserApproved(), relatedUser.getId());
									})
									.collect(Collectors.toList()));
						}
					} else {
						// If the user is a related user, add only their details
						onboardingRequest.getRelatedUsers().stream()
								.filter(relatedUser -> relatedUser.getUserId().equals(userId)
										&& !RelatedType.CO_ADMIN.equals(relatedUser.getRelatedType()))
								.forEach(relatedUser -> {
									UserDTO userDetails = userDetailsMap.get(userId);

									dtos.add(FlatOnboardStatusDto.buildFlatOwnerStatusDto(onboardingRequest, userDetails,
											relatedUser.getRelatedType().name(), relatedUser.isRelatedUserApproved(), relatedUser.getId()));
								});
					}
					return dtos.stream();
				})
				.collect(Collectors.toList());
	}
	
	@Override
	public Map<String, Object> getFlatsWithOutOwners(Long apartmentId, int pageNo, int pageSize){
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<NivaasFlatModel> nivaasFlats = flatService.getFlatsByApartment(apartmentId, pageable);

		List<FlatDTO> list = new ArrayList<FlatDTO>();
		if (!CollectionUtils.isEmpty(nivaasFlats.getContent())) {
			for (NivaasFlatModel nivaasFlatModel : nivaasFlats.getContent()) {
				if (null == nivaasFlatModel.getOwnerId()
						&& null != nivaasFlatModel.getFlatNo()) {
					FlatDTO flatDTO = new FlatDTO();
					flatDTO.setId(nivaasFlatModel.getId());
					flatDTO.setFlatNo(nivaasFlatModel.getFlatNo());
					flatDTO.setIsAvailableForRent(nivaasFlatModel.isAvailableForRent());
					flatDTO.setIsAvailableForSale(nivaasFlatModel.isAvailableForSale());
					flatDTO.setIsParkingAvailable(nivaasFlatModel.isParkingAvailable());
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

			return response;

		}
		return Collections.emptyMap();
	}
	
	public void flatOwnerOnboardingRequest(OnboardingRequestDTO onboardingRequestDTO, NivaasFlatModel flatModel,
			OnboardType onboardType) {
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		OnboardingRequest onboardingRequest = findByFlatAndAdminApproved(flatModel);
		if (onboardingRequest != null) {
			log.warn("Did not find valid onboarding request for {}", flatModel.getId());
			throw new NivaasCustomerException(ErrorCode.NOT_VALID);
		}

		Boolean isRequestAlreadyExists = onboardingRequestRepository.existsByFlatAndOnboardTypeAndRequestedCustomer(flatModel,
				onboardType, loggedInUser.getId());
		if (isRequestAlreadyExists) {
			log.warn("Request already exists for user {} flat {}", loggedInUser.getId(), flatModel.getId());
			throw new NivaasCustomerException(ErrorCode.DUPLICATE);
		}
		OnboardingRequest request = new OnboardingRequest();
		request.setApartment(flatModel.getApartment());
		request.setCreationTime(new Date());
		request.setFlat(flatModel);
		request.setOnboardType(onboardType);
		request.setRequestedCustomer(loggedInUser.getId());

		onboardingRequestRepository.save(request);
		
		buildAndSendTenantNotification(flatModel, loggedInUser.getId(), NotificationType.FLAT_ONBOARD);
	}
	
	@Override
	public void approveFlatOwner(final OnboardingRequest onboardingRequest,final Long userId,final NivaasFlatModel flatModel,
			final OnboardType onboardType) {
		
		NivaasFlatModel nivaasFlatModel = flatService.findById(flatModel.getId());
		if(Objects.nonNull(nivaasFlatModel) && null != nivaasFlatModel.getOwnerId()) {
			throw new NivaasCustomerException(ErrorCode.FLAT_ALREADY_APPROVED);
		}
		
		onboardingRequest.setAdminApproved(Boolean.TRUE);
		onboardingRequest.setApprovedOn(new Date());
		onboardingRequest.setStatus(Boolean.TRUE);
		onboardingRequestRepository.save(onboardingRequest);
		
		flatModel.setOwnerId(onboardingRequest.getRequestedCustomer());
		flatService.save(flatModel);
		accessMgmtClient.addRole(onboardingRequest.getRequestedCustomer(), ERole.ROLE_FLAT_OWNER);
		
		buildAndSendTenantNotification(onboardingRequest.getFlat(), userId, NotificationType.FLAT_APPROVED);
	}
}
