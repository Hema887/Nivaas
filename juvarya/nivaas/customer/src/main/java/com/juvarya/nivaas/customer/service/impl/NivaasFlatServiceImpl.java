package com.juvarya.nivaas.customer.service.impl;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import com.juvarya.nivaas.customer.dto.request.FlatNumbersOnboardDto;
import com.juvarya.nivaas.customer.dto.request.NivaasBasicFlatDTO;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.commonservice.dto.BasicOnboardUserDTO;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.customer.client.AccessMgmtClient;
import com.juvarya.nivaas.customer.dto.FlatBasicDTO;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.customer.repository.ApartmentUserRoleRepository;
import com.juvarya.nivaas.customer.repository.NivaasApartmentRepository;
import com.juvarya.nivaas.customer.repository.NivaasFlatRepository;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.NivaasFlatService;
import com.juvarya.nivaas.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class NivaasFlatServiceImpl implements NivaasFlatService {

	@Autowired
	private NivaasFlatRepository nivaasFlatRepository;

	@Autowired
	private NivaasApartmentRepository apartmentRepository;

	@Autowired
	private ApartmentUserRoleRepository apartmentUserRoleRepository;

	@Autowired
	private AccessMgmtClient accessMgmtClient;

	@Transactional
	@Override
	public NivaasFlatModel save(NivaasFlatModel nivaasFlatModel) {
		return nivaasFlatRepository.save(nivaasFlatModel);
	}

	@Transactional
	@Override
	public void saveAll(List<NivaasFlatModel> nivaasFlatModels) {
		nivaasFlatRepository.saveAll(nivaasFlatModels);
	}

	@SuppressWarnings("rawtypes")
	@Transactional
	@Override
	public ResponseEntity updateBasicFlatDetails(final Long apartmentId, final Long flatId,
			final FlatBasicDTO flatBasicDTO) {
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		log.info("Starting update of basic flat details: apartmentId={}, flatId={}, flatBasicDTO={}", apartmentId,
				flatId, flatBasicDTO);

		log.debug("LoggedInUser: {}", loggedInUser.getId());

		Optional<NivaasApartmentModel> jtApartmentModel = apartmentRepository.findById(apartmentId);
		if (jtApartmentModel.isEmpty()) {
			log.warn("Apartment not found: {}", apartmentId);
			throw new NivaasCustomerException(ErrorCode.APARTMENT_NOT_FOUND);
		}

		ApartmentUserRoleModel apartmentUserRoleModel = apartmentUserRoleRepository
				.findByApartmentModelAndCustomerId(jtApartmentModel.get(), loggedInUser.getId());

		if (null != apartmentUserRoleModel) {
			log.info("User has apartment role: {}", apartmentUserRoleModel);
			Optional<NivaasFlatModel> flatModel = nivaasFlatRepository.findByApartmentAndFlatId(apartmentId, flatId);
			if (flatModel.isPresent()) {

				NivaasFlatModel flat = flatModel.get();
				Long ownerId = flatModel.get().getOwnerId();
				UserDTO owner = accessMgmtClient.getUserById(ownerId);
				log.debug("Current flat model: {} Current ownerId: {}", flat, ownerId);
				if (!flatBasicDTO.getOwnerPhoneNo().equals(owner.getPrimaryContact())) {
					List<NivaasFlatModel> sameOwnerForOtherFlats = nivaasFlatRepository.findByOwnerNotInFlat(ownerId,
							flatId);
					log.debug("Same ownerId for other flats: {}", sameOwnerForOtherFlats);
					if (CollectionUtils.isEmpty(sameOwnerForOtherFlats)) {
						// Remove flat ownerId role
						log.info("Removed flat ownerId role from: {}", owner.getPrimaryContact());
						accessMgmtClient.removeUserRole(owner.getPrimaryContact(), ERole.ROLE_FLAT_OWNER);
					}
					BasicOnboardUserDTO basicOnboardUserDTO = BasicOnboardUserDTO.builder()
							.userRoles(Set.of(ERole.ROLE_USER, ERole.ROLE_FLAT_OWNER))
							.fullName(flatBasicDTO.getOwnerName()).primaryContact(flatBasicDTO.getOwnerPhoneNo())
							.build();
					Long flatOwner = accessMgmtClient.onBoardUser(basicOnboardUserDTO);
					flat.setOwnerId(flatOwner);
					log.info("Onboarded new flat ownerId: {}", flatOwner);
				} else if (!flatBasicDTO.getOwnerName().equals(owner.getFullName())) {
					// update user name
					UserDTO userOwner = accessMgmtClient.getUserById(ownerId);
					userOwner.setFullName(flatBasicDTO.getOwnerName());
					accessMgmtClient.saveUser(userOwner);
					flat.setOwnerId(ownerId);
					log.info("Updated ownerId's name: {}", userOwner);
				}

				if (!flatBasicDTO.getFlatNo().equals(flat.getFlatNo())) {
					boolean flatNoExists = nivaasFlatRepository.existsByApartmentIdAndFlatNo(apartmentId,
							flatBasicDTO.getFlatNo());
					if (flatNoExists) {
						log.warn("Duplicate flat number found: {}", flatBasicDTO.getFlatNo());
						throw new NivaasCustomerException(ErrorCode.FLAT_ALREADY_EXISTS);
					}
					log.info("Updated flat number: {}", flatBasicDTO.getFlatNo());
					flat.setFlatNo(flatBasicDTO.getFlatNo());
				}
				nivaasFlatRepository.save(flat);
				log.info("Saved updated flat model: {}", flat);
				return ResponseEntity.ok(new MessageResponse("Flats updated"));
			}
		}
		log.warn("User is not allowed to update flat details: user={}", loggedInUser.getId());
		throw new NivaasCustomerException(ErrorCode.FLAT_ONBOARD_NOT_ALLOWED);
	}

	@Transactional
	@Override
	public void removeFlat(NivaasFlatModel nivaasFlatModel) {
		nivaasFlatRepository.delete(nivaasFlatModel);

	}

	@Override
	public NivaasFlatModel findById(Long id) {
		Optional<NivaasFlatModel> flat = nivaasFlatRepository.findById(id);
		return flat.orElse(null);

	}

	@Override
	public Page<NivaasFlatModel> getAll(Pageable pageable) {
		return nivaasFlatRepository.findAll(pageable);
	}

	@Override
	public Page<NivaasFlatModel> getFlatsByApartment(Long apartmentId, Pageable page) {
		return nivaasFlatRepository.getFlatsByApartment(apartmentId, page);
	}

	@Override
	public List<NivaasFlatModel> getAllFlatsByApartment(Long apartmentId) {
		return nivaasFlatRepository.getAllFlatsByApartment(apartmentId);
	}

	@Override
	public Map<Long, String> getFlatsMapByApartment(Long apartmentId) {
		List<NivaasFlatModel> flatModels = nivaasFlatRepository.getAllFlatsByApartment(apartmentId);
		return flatModels.stream().collect(Collectors.toMap(NivaasFlatModel::getId, NivaasFlatModel::getFlatNo));
	}

	@Override
	public List<NivaasFlatModel> findByOwner(Long id) {
		return nivaasFlatRepository.findByOwnerORTenant(id);
	}

	@Override
	public Optional<NivaasFlatModel> findByJtApartmentAndFlatId(Long jtApartmentId, Long flatId) {
		return nivaasFlatRepository.findByApartmentAndFlatId(jtApartmentId, flatId);
	}

	@Override
	public boolean checkFlatExists(Long apartmentId, String flatNo) {
		return nivaasFlatRepository.existsByApartmentIdAndFlatNo(apartmentId, flatNo);
	}

	@Override
	@Transactional
	public void bulkAddWithoutOwners(final FlatNumbersOnboardDto withoutOwnerDto) {
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		final Long userId = loggedInUser.getId();
		log.info("Starting bulk add with out owner details for flats: {} LoggedInUser: {}", withoutOwnerDto, userId);
		final Long apartmentId = withoutOwnerDto.getApartmentId();
		Optional<NivaasApartmentModel> apartmentModel = apartmentRepository.findById(apartmentId);
		if (apartmentModel.isEmpty()) {
			log.warn("Apartment not found: {}", apartmentId);
			throw new NivaasCustomerException(ErrorCode.APARTMENT_NOT_FOUND);
		}
		log.debug("NivaasApartmentModel: {}", apartmentModel.get());

		ApartmentUserRoleModel apartmentUserRoleModel = apartmentUserRoleRepository
				.findByApartmentModelAndCustomerId(apartmentModel.get(), userId);
		if (null == apartmentUserRoleModel) {
			log.warn("User is not allowed to onboard flats: {}", userId);
			throw new NivaasCustomerException(ErrorCode.FLAT_ONBOARD_NOT_ALLOWED);
		}
		int onboardFlats = withoutOwnerDto.getFlats().size();
		List<NivaasFlatModel> flatModels = getAllFlatsByApartment(apartmentId);
		if (flatModels.size() + onboardFlats > apartmentModel.get().getTotalFlats()) {
			throw new NivaasCustomerException(ErrorCode.FLAT_LIMIT,
					ErrorCode.FLAT_LIMIT.formatMessage(apartmentModel.get().getTotalFlats()));
		}
		log.info("User has apartment role: {}", apartmentUserRoleModel);
		List<NivaasFlatModel> flatModelList = new ArrayList<>();
		withoutOwnerDto
				.getFlats().stream().filter(flatBasicDTO -> !checkFlatExists(apartmentId, flatBasicDTO.getFlatNo()))
				.collect(Collectors.toMap(
						NivaasBasicFlatDTO::getFlatNo,
						flatBasicDTO -> flatBasicDTO,
						(existing, replacement) -> existing
				))
				.values()
				.forEach(flatBasicDTO -> {
					NivaasFlatModel flatModel = new NivaasFlatModel();
					// By-default setting flat is available for rent
					flatModel.setAvailableForRent(true);
					flatModel.setFlatNo(flatBasicDTO.getFlatNo());
					flatModel.setApartment(apartmentModel.get());

					flatModelList.add(flatModel);
				});
		saveAll(flatModelList);
		log.info("Saved flat models: {}", flatModelList);
	}

}
