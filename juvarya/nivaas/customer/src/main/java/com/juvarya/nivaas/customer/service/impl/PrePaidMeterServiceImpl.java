package com.juvarya.nivaas.customer.service.impl;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.customer.dto.JTPrePaidMeterDTO;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.PrepaidMeterModel;
import com.juvarya.nivaas.customer.populator.PrePaidMeterPopulator;
import com.juvarya.nivaas.customer.repository.ApartmentUserRoleRepository;
import com.juvarya.nivaas.customer.repository.NivaasApartmentRepository;
import com.juvarya.nivaas.customer.repository.PrepaidMeterRepository;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.service.PrePaidMeterService;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.SecurityUtils;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@Service
@Slf4j
public class PrePaidMeterServiceImpl extends JTBaseEndpoint implements PrePaidMeterService {

	@Autowired
	private PrepaidMeterRepository prepaidMeterRepository;

	@Autowired
	private NivaasApartmentRepository apartmentRepository;

	@Autowired
	private ApartmentUserRoleRepository apartmentUserRoleRepository;

	@Autowired
	private PrePaidMeterPopulator prePaidMeterPopulator;

	@Autowired
	private NivaasApartmentService nivaasApartmentService;

	@Override
	@Transactional
	public ResponseEntity save(JTPrePaidMeterDTO jtPrePaidDTO) {
		log.info("Saving PrePaid Meter: {}", jtPrePaidDTO);
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();

		Optional<NivaasApartmentModel> jtAprtmentModel = apartmentRepository.findById(jtPrePaidDTO.getApartmentId());
		if (jtAprtmentModel.isEmpty()) {
			log.warn("Apartment not found for ID: {}", jtPrePaidDTO.getApartmentId());
			throw new NivaasCustomerException(ErrorCode.APARTMENT_NOT_FOUND);
		}

		ApartmentUserRoleModel apartmentUserRoleModel = apartmentUserRoleRepository
				.findByApartmentModelAndCustomerId(jtAprtmentModel.get(), loggedInUser.getId());

		if (null != apartmentUserRoleModel
				&& apartmentUserRoleModel.getRoleName().equals(ERole.ROLE_APARTMENT_ADMIN.name())) {

			PrepaidMeterModel jtPrePaidModel = new PrepaidMeterModel();

			if (null != jtPrePaidDTO.getName()) {
				PrepaidMeterModel prePaidMeterModel = prepaidMeterRepository
						.getByNameAndApartmentModel(jtPrePaidDTO.getName(), jtPrePaidDTO.getApartmentId());
				if (Objects.nonNull(prePaidMeterModel)) {
					log.warn("PrePaid Meter with name '{}' already exists", jtPrePaidDTO.getName());
					return ResponseEntity.ok(new MessageResponse("Already Exists This Name"));
				}
			}
			long count = prepaidMeterRepository.countByApartmentModel(jtAprtmentModel.get());
			if (count >= 5) {
				log.warn("Limit reached: Only 5 Prepaid Meters allowed for apartment {}",
						jtPrePaidDTO.getApartmentId());
				return ResponseEntity.ok(new MessageResponse("Limit reached: Only 5 Prepaid Meters allowed"));
			}
			jtPrePaidModel.setId(jtPrePaidDTO.getId());
			jtPrePaidModel.setCreationTime(new Date());
			jtPrePaidModel.setName(jtPrePaidDTO.getName());
			jtPrePaidModel.setCostPerUnit(jtPrePaidDTO.getCostPerUnit());
			jtPrePaidModel.setDescription(jtPrePaidDTO.getDescription());
			jtPrePaidModel.setApartmentModel(jtAprtmentModel.get());
			prepaidMeterRepository.save(jtPrePaidModel);
			log.info("PrePaid Meter Added");
			return ResponseEntity.ok(new MessageResponse("PrePaid Meter Added"));
		}
		log.warn("User is not allowed to add PrePaid Meter");
		throw new NivaasCustomerException(ErrorCode.PREPAID_METER_CREATION_NOT_ALLOWED);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResponseEntity findById(Long id)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Fetching PrePaid Meter by ID: {}", id);
		Optional<PrepaidMeterModel> jtPrepaid = prepaidMeterRepository.findById(id);
		if (jtPrepaid.isPresent()) {
			JTPrePaidMeterDTO jtPrePaidMeterDTO = (JTPrePaidMeterDTO) getConverterInstance().convert(jtPrepaid.get());
			log.debug("Found PrePaid Meter: {}", jtPrePaidMeterDTO);
			return ResponseEntity.ok(jtPrePaidMeterDTO);
		}
		log.warn("PrePaid Meter not found for ID: {}", id);
		throw new NivaasCustomerException(ErrorCode.PREPAID_METER_NOT_FOUND);
	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		return getConverter(prePaidMeterPopulator, JTPrePaidMeterDTO.class.getName());
	}

	@Override
	@Transactional
	public ResponseEntity delete(Long id) {
		log.info("Deleting PrePaid Meter with ID: {}", id);
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		Optional<PrepaidMeterModel> jtPrepaid = prepaidMeterRepository.findById(id);
		if (jtPrepaid.isEmpty()) {
			log.warn("PrePaid Meter not found for ID: {}", id);
			throw new NivaasCustomerException(ErrorCode.PREPAID_METER_NOT_FOUND);
		}

		ApartmentUserRoleModel apartmentUserRoleModel = apartmentUserRoleRepository
				.findByApartmentModelAndCustomerId(jtPrepaid.get().getApartmentModel(), loggedInUser.getId());
		if (null != apartmentUserRoleModel
				&& apartmentUserRoleModel.getRoleName().equals(ERole.ROLE_APARTMENT_ADMIN.name())) {
			prepaidMeterRepository.delete(jtPrepaid.get());
			log.info("PrePaid Meter Deleted");
			return ResponseEntity.ok(new MessageResponse("Deleted"));
		}
		log.warn("User is not allowed to delete PrePaid Meter");
		throw new NivaasCustomerException(ErrorCode.PREPAID_METER_DELETE_NOT_ALLOWED);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ResponseEntity getPrePaidMeterList(Long apartmentId, int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Fetching PrePaid Meter list for apartment {} (Page {}, Size {})", apartmentId, pageNo, pageSize);
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		NivaasApartmentModel nivaasApartmentModel = nivaasApartmentService.findById(apartmentId);
		if (Objects.isNull(nivaasApartmentModel)) {
			log.warn("Apartment not found for ID: {}", apartmentId);
			return ResponseEntity.ok(new MessageResponse("Apartment Not Found"));
		}

		Page<PrepaidMeterModel> prePaids = prepaidMeterRepository.findByapartmentModel(nivaasApartmentModel, pageable);

		if (!CollectionUtils.isEmpty(prePaids.getContent())) {
			Map<String, Object> response = new HashMap<>();

			response.put(NivaasConstants.CURRENT_PAGE, prePaids.getNumber());
			response.put(NivaasConstants.TOTAL_ITEMS, prePaids.getTotalElements());
			response.put(NivaasConstants.TOTAL_PAGES, prePaids.getTotalPages());
			response.put(NivaasConstants.PAGE_NUM, pageNo);
			response.put(NivaasConstants.PAGE_SIZE, pageSize);
			response.put(NivaasConstants.PROFILES, getConverterInstance().convertAll(prePaids.getContent()));
			log.debug("Fetched {} PrePaid Meters", prePaids.getNumberOfElements());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}
		log.warn("No PrePaid Meters found for apartment {}", apartmentId);
		throw new NivaasCustomerException(ErrorCode.NO_PREPAID_METERS_FOUND);
	}

	@Override
	@org.springframework.transaction.annotation.Transactional
	public ResponseEntity updatePrePaidMeter(JTPrePaidMeterDTO jtPrePaidMeterDTO) {
		log.info("Updating PrePaid Meter: {}", jtPrePaidMeterDTO);
		if (null == jtPrePaidMeterDTO.getId()) {
			log.warn("PrepaidId should not be null");
			throw new NivaasCustomerException(ErrorCode.PREPAID_METER_NOT_FOUND);
		}
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();

		Optional<PrepaidMeterModel> jtPrePaid = prepaidMeterRepository.findById(jtPrePaidMeterDTO.getId());

		if (jtPrePaid.isPresent()) {
			PrepaidMeterModel jtPrePaidModel = jtPrePaid.get();

			ApartmentUserRoleModel apartmentUserRoleModel = apartmentUserRoleRepository
					.findByApartmentModelAndCustomerId(jtPrePaidModel.getApartmentModel(), loggedInUser.getId());

			if (null != apartmentUserRoleModel
					&& apartmentUserRoleModel.getRoleName().equals(ERole.ROLE_APARTMENT_ADMIN.name())) {
				if (!jtPrePaidModel.getName().equals(jtPrePaidMeterDTO.getName())) {
					PrepaidMeterModel prePaidMeterModel = prepaidMeterRepository.getByNameAndApartmentModel(
							jtPrePaidMeterDTO.getName(), jtPrePaidMeterDTO.getApartmentId());
					if (Objects.nonNull(prePaidMeterModel)) {
						log.warn("PrePaid Meter with name '{}' already exists", jtPrePaidMeterDTO.getName());
						throw new NivaasCustomerException(ErrorCode.NAME_ALREADY_EXISTS);
					}
					jtPrePaidModel.setName(jtPrePaidMeterDTO.getName());
				}
				jtPrePaidModel.setCreationTime(new Date());

				if (null != jtPrePaidMeterDTO.getCostPerUnit()) {
					jtPrePaidModel.setCostPerUnit(jtPrePaidMeterDTO.getCostPerUnit());
				}

				if (null != jtPrePaidMeterDTO.getDescription()) {
					jtPrePaidModel.setDescription(jtPrePaidMeterDTO.getDescription());
				}
				prepaidMeterRepository.save(jtPrePaidModel);
				log.info("PrePaid Meter Updated");
				return ResponseEntity.ok(new MessageResponse("PrePaid Meter Updated"));
			}
			log.warn("User is not allowed to update PrePaid Meter");
			throw new NivaasCustomerException(ErrorCode.PREPAID_METER_UPDATE_NOT_ALLOWED);
		}
		log.warn("PrePaidMeter Id Is Required");
		throw new NivaasCustomerException(ErrorCode.PREPAID_METER_REQUIRED);
	}

}
