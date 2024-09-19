package com.juvarya.nivaas.customer.service.impl;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.customer.dto.request.PrepaidConsumptionDto;
import com.juvarya.nivaas.customer.dto.response.ConsumptionPerFlatDTO;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.customer.model.PrepaidFlatUsageModel;
import com.juvarya.nivaas.customer.repository.ApartmentUserRoleRepository;
import com.juvarya.nivaas.customer.repository.PrepaidFlatUsageRepository;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.service.NivaasFlatService;
import com.juvarya.nivaas.customer.service.PrepaidFlatUsageService;
import com.juvarya.nivaas.customer.util.UserRoleHelper;
import com.juvarya.nivaas.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@Service
@Slf4j
public class PrepaidFlatUsageServiceImpl implements PrepaidFlatUsageService {

	@Autowired
	private PrepaidFlatUsageRepository prepaidFlatUsageRepository;

	@Autowired
	private NivaasFlatService nivaasFlatService;

	@Autowired
	private NivaasApartmentService nivaasApartmentService;

	@Autowired
	private ApartmentUserRoleRepository apartmentUserRoleRepository;

	@Autowired
	private UserRoleHelper userRoleHelper;

	@Override
	public ResponseEntity getFlatUsage(final Long apartmentId, final Long prepaidId) {
		log.info("Fetching flat usage for apartment {} and prepaid meter {}", apartmentId, prepaidId);
		UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();
		log.debug("Logged-in user: {}", user);
		NivaasApartmentModel nivaasApartmentModel = nivaasApartmentService.findById(apartmentId);
		if (Objects.isNull(nivaasApartmentModel)
				|| !userRoleHelper.isValidApartmentAdmin(user.getId(), nivaasApartmentModel)) {
			log.warn("Invalid request: Apartment not found or user is not authorized");
			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		List<PrepaidFlatUsageModel> prepaidFlatUsageModels = prepaidFlatUsageRepository
				.findByPrepaidMeterIdAndApartmentId(prepaidId, apartmentId);
		Map<Long, PrepaidFlatUsageModel> flatUsageMap = prepaidFlatUsageModels.stream().collect(Collectors
				.toMap(PrepaidFlatUsageModel::getFlatId, model -> model, (existing, replacement) -> replacement // override
																												// if
																												// multiple
																												// entries
				));
		List<NivaasFlatModel> flatModels = nivaasFlatService.getAllFlatsByApartment(apartmentId);
		List<ConsumptionPerFlatDTO> consumptionPerFlatDTOS = new ArrayList<>();
		flatModels.forEach(flatModel -> {
			PrepaidFlatUsageModel flatUsageModel = flatUsageMap.get(flatModel.getId());
			ConsumptionPerFlatDTO consumptionPerFlatDTO = ConsumptionPerFlatDTO.builder().flatId(flatModel.getId())
					.flatNumber(flatModel.getFlatNo())
					.unitsConsumed(flatUsageModel != null ? flatUsageModel.getUnitsConsumed() : 0).build();
			consumptionPerFlatDTOS.add(consumptionPerFlatDTO);
		});
		log.debug("Flat usage fetched successfully");
		return ResponseEntity.ok(consumptionPerFlatDTOS);
	}

	@Override
	@Transactional
	public ResponseEntity updateConsumed(PrepaidConsumptionDto prepaidConsumptionDto) {
		log.info("Updating consumed units for prepaid meter {} in apartment {}", prepaidConsumptionDto.getPrepaidId(),
				prepaidConsumptionDto.getApartmentId());
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		log.debug("Logged-in user: {}", loggedInUser.getId());

		NivaasApartmentModel nivaasApartmentModel = nivaasApartmentService.findById(prepaidConsumptionDto.getApartmentId());
		if (Objects.isNull(nivaasApartmentModel)) {
			log.warn("Apartment not found for ID: {}", prepaidConsumptionDto.getApartmentId());
			throw new NivaasCustomerException(ErrorCode.APARTMENT_NOT_FOUND);
		}

		ApartmentUserRoleModel apartmentUserRoleModel = apartmentUserRoleRepository
				.findByApartmentModelAndCustomerId(nivaasApartmentModel, loggedInUser.getId());

		if (null != apartmentUserRoleModel) {
			List<PrepaidFlatUsageModel> prePaidFlatMeters = new ArrayList<>();

			prepaidConsumptionDto.getFlatConsumption().forEach(flatConsumptionDTO -> {
				Optional<NivaasFlatModel> flat = nivaasFlatService.findByJtApartmentAndFlatId(
						prepaidConsumptionDto.getApartmentId(), flatConsumptionDTO.getFlatId());
				if (flat.isPresent()) {
					Optional<PrepaidFlatUsageModel> existingPrepaidList = prepaidFlatUsageRepository
							.findByFlatIdAndPrepaidMeterId(flat.get().getId(), prepaidConsumptionDto.getPrepaidId());

					PrepaidFlatUsageModel prepaid;
					if (existingPrepaidList.isPresent()) {
						prepaid = existingPrepaidList.get();
					} else {
						prepaid = new PrepaidFlatUsageModel();
						prepaid.setCreationTime(new Date());
						prepaid.setPrepaidMeterId(prepaidConsumptionDto.getPrepaidId());
					}
					prepaid.setFlatId(flat.get().getId());
					prepaid.setApartmentId(prepaidConsumptionDto.getApartmentId());
					prepaid.setUnitsConsumed(flatConsumptionDTO.getUnitsConsumed());
					prePaidFlatMeters.add(prepaid);
				}
			});
			prepaidFlatUsageRepository.saveAll(prePaidFlatMeters);
			log.info("Consumption Added/Updated");
			return ResponseEntity.ok().body(new MessageResponse("Consumption Added/Updated"));
		}
		log.warn("User is not allowed to update consumption");
		throw new NivaasCustomerException(ErrorCode.CONSUMPTION_NOT_ALLOWED);
	}

	@Override
	@Transactional
	public PrepaidFlatUsageModel save(PrepaidFlatUsageModel prePaidFlatMeterModel) {
		log.info("Saving prepaid flat usage for flat {}", prePaidFlatMeterModel.getFlatId());
		return prepaidFlatUsageRepository.save(prePaidFlatMeterModel);
	}
}
