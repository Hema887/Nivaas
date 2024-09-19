package com.juvarya.nivaas.customer.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.commonservice.dto.AddressDTO;
import com.juvarya.nivaas.commonservice.dto.ApartmentDTO;
import com.juvarya.nivaas.commonservice.dto.NivaasCityDTO;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.customer.client.AccessMgmtClient;
import com.juvarya.nivaas.customer.client.NivaasCoreClient;
import com.juvarya.nivaas.customer.dto.request.ApartmentCoAdminDTO;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.customer.model.constants.ApartmentType;
import com.juvarya.nivaas.customer.populator.ApartmentPopulator;
import com.juvarya.nivaas.customer.repository.NivaasApartmentRepository;
import com.juvarya.nivaas.customer.service.ApartmentUserRoleService;
import com.juvarya.nivaas.customer.service.CurrentApartmentService;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.service.NivaasFlatService;
import com.juvarya.nivaas.customer.service.OnboardingRequestService;
import com.juvarya.nivaas.customer.util.UserRoleHelper;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.SecurityUtils;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@Service
@Slf4j
public class NivaasApartmentServiceImpl extends JTBaseEndpoint implements NivaasApartmentService {

	@Autowired
	private NivaasApartmentRepository apartmentRepository;

	@Autowired
	private ApartmentPopulator apartmentPopulator;

	@Autowired
	private UserRoleHelper userRoleHelper;

	@Autowired
	private NivaasFlatService flatService;

	@Autowired
	private ApartmentUserRoleService apartmentUserRoleService;

	@Autowired
	private OnboardingRequestService jtonboardingRequestService;

	@Autowired
	private AccessMgmtClient accessMgmtClient;

	@Autowired
	private NivaasCoreClient nivaasCoreClient;

	@Autowired
	private CurrentApartmentService currentApartmentService;

	@Transactional
	@Override
	public NivaasApartmentModel saveApartment(NivaasApartmentModel nivaasApartmentModel) {
		log.info("Saving apartment: {}", nivaasApartmentModel.getName());
		return apartmentRepository.save(nivaasApartmentModel);
	}

	@Transactional
	@Override
	public NivaasApartmentModel sendOnBoardRequestForApartment(final ApartmentDTO apartmentDTO, final Long customerId) {

		log.info("Onboard new apartment request {}", apartmentDTO.getName());
		
		NivaasCityDTO nivaasCityModel = nivaasCoreClient.getCityDetails(apartmentDTO.getCityId());
		if (Objects.isNull(nivaasCityModel)) {
			log.warn("City not found with ID: {}", apartmentDTO.getCityId());
			throw new NivaasCustomerException(ErrorCode.CITY_NOT_FOUND);
		}

		log.info("Creating address for apartment: {}", apartmentDTO.getName());
		UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
		AddressDTO addressModel = AddressDTO.builder().createdById(userDetails.getId()).cityId(apartmentDTO.getCityId())
				.line1(apartmentDTO.getLine1()).line2(apartmentDTO.getLine2()).line3(apartmentDTO.getLine3())
				.creationTime(new Date()).build();

		if (!CollectionUtils.isEmpty(nivaasCityModel.getCodes())) {
			for (String codeDTO : nivaasCityModel.getCodes()) {
				addressModel.setPostalCode(codeDTO);
			}
		}

		addressModel = nivaasCoreClient.saveAddress(addressModel);
		log.info("Address created with ID: {}", addressModel.getId());

		log.info("Creating NivaasApartmentModel for apartment: {}", apartmentDTO.getName());
		NivaasApartmentModel nivaasApartmentModel = new NivaasApartmentModel();
		nivaasApartmentModel.setName(apartmentDTO.getName());
		nivaasApartmentModel.setCode(apartmentDTO.getCode());
		nivaasApartmentModel.setTotalFlats(apartmentDTO.getTotalFlats());
		nivaasApartmentModel.setCreatedBy(userDetails.getId());
		nivaasApartmentModel.setBuilderName(apartmentDTO.getBuilderName());
		nivaasApartmentModel.setDescription(apartmentDTO.getDescription());
		nivaasApartmentModel.setApartmentType(apartmentType(apartmentDTO.getApartmentType()));
		nivaasApartmentModel.setAddress(addressModel.getId());
		log.info("Triggering onboarding process for apartment: {}", apartmentDTO.getName());

		jtonboardingRequestService.onBoardApartmentAdmin(nivaasApartmentModel, customerId);
		log.info("Apartment onboarded successfully: {}", nivaasApartmentModel.getName());

		NivaasApartmentModel apartmentModel = saveApartment(nivaasApartmentModel);
		currentApartmentService.setCurrentApartmentIfNotExists(customerId, nivaasApartmentModel.getId());
		return apartmentModel;
	}

	@Override
	public NivaasApartmentModel findById(Long id) {
		log.info("Finding apartment by ID: {}", id);
		Optional<NivaasApartmentModel> apartment = apartmentRepository.findById(id);
		return apartment.orElse(null);
	}

	@Override
	@Transactional
	public void removeApartment(NivaasApartmentModel nivaasApartmentModel) {
		log.info("Removing apartment: {}", nivaasApartmentModel.getName());
		apartmentRepository.delete(nivaasApartmentModel);

	}

	@Override
	public List<NivaasApartmentModel> findAll() {
		log.info("Finding all apartments");
		return apartmentRepository.findAll();
	}

	@Override
	public List<NivaasApartmentModel> findByCreatedBy(Long createdBy) {
		log.info("Finding apartments created by user ID: {}", createdBy);
		return apartmentRepository.findByCreatedBy(createdBy);
	}

	@SuppressWarnings("unchecked")
	public List<ApartmentDTO> findbyApartmentName(String name)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Finding apartments by name: {}", name);
		List<NivaasApartmentModel> apartment = apartmentRepository.findByNameContainingIgnoreCase(name);
		List<ApartmentDTO> apartmentDTO = getConverterInstance().convertAll(apartment);
		return apartmentDTO;
	}

	@Transactional
	public void addCoAdmin(final ApartmentCoAdminDTO coAdminDTO) {
		log.info("Adding co-admin with User ID: {}", coAdminDTO.getUserId());
		UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();
		NivaasApartmentModel nivaasApartmentModel = findById(coAdminDTO.getApartmentId());
		if (!Objects.isNull(nivaasApartmentModel)
				&& userRoleHelper.isValidApartmentAdmin(userDetails.getId(), nivaasApartmentModel)) {
			List<NivaasFlatModel> flatModels = flatService.getAllFlatsByApartment(nivaasApartmentModel.getId());
			Optional<Long> validUserRequest = flatModels.stream().map(NivaasFlatModel::getOwnerId)
					.filter(user -> coAdminDTO.getUserId().equals(user)).findFirst();
			validUserRequest.ifPresent(userId -> {
				accessMgmtClient.addRole(userId, coAdminDTO.getUserRole());
				if (ERole.ROLE_APARTMENT_ADMIN == coAdminDTO.getUserRole()
						&& !userRoleHelper.isValidApartmentAdmin(validUserRequest.get(), nivaasApartmentModel)) {
					apartmentUserRoleService.onBoardApartmentAdminOrHelper(nivaasApartmentModel, validUserRequest.get(),
							coAdminDTO.getUserRole().name());
					jtonboardingRequestService.onBoardCoAdmin(nivaasApartmentModel.getId(), userId);
				}
			});
		}
	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		log.debug("Getting converter instance");
		return getConverter(apartmentPopulator, ApartmentDTO.class.getName());
	}

	public static ApartmentType apartmentType(String type) {
		if (ApartmentType.MULTIBLOCK.name().equalsIgnoreCase(type)) {
			return ApartmentType.MULTIBLOCK;
		}
		return ApartmentType.SINGLE;
	}

	@SuppressWarnings("unchecked")
	@Override
	public Map<String, Object> nearyByApartments(Long cityId, int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {

		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<NivaasApartmentModel> apartments = apartmentRepository.findAll(pageable);

		if (CollectionUtils.isEmpty(apartments.getContent())) {
			return null;
		}

		Map<String, Object> response = new HashMap<>();
		List<ApartmentDTO> apartmentDTOs = new ArrayList<>();
		List<AddressDTO> addresses = nivaasCoreClient.getAddressByCity(cityId);
		if (!CollectionUtils.isEmpty(addresses)) {
			for (NivaasApartmentModel apartmentModel : apartments.getContent()) {
				for (AddressDTO addressDTO : addresses) {
					if (apartmentModel.getAddress().equals(addressDTO.getId())
							&& Boolean.TRUE.equals(apartmentModel.isApprove())) {
						ApartmentDTO apartmentDTO = (ApartmentDTO) getConverterInstance().convert(apartmentModel);
						apartmentDTOs.add(apartmentDTO);
					}
				}
			}
			response.put(NivaasConstants.TOTAL_ITEMS, apartmentDTOs.size());
			response.put(NivaasConstants.TOTAL_PAGES, apartments.getTotalPages());
			response.put(NivaasConstants.PAGE_NUM, pageNo);
			response.put(NivaasConstants.PAGE_SIZE, pageSize);
			response.put(NivaasConstants.PROFILES, apartmentDTOs);
			return response;
		}
		return null;
	}
}
