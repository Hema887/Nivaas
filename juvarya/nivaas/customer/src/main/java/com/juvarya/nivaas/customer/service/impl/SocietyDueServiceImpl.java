package com.juvarya.nivaas.customer.service.impl;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.juvarya.nivaas.customer.dto.FlatUsageDto;
import com.juvarya.nivaas.customer.dto.SocietyDTO;
import com.juvarya.nivaas.customer.firebase.listeners.NotificationPublisher;
import com.juvarya.nivaas.customer.model.MaintenanceModel;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.customer.model.NotificationModel;
import com.juvarya.nivaas.customer.model.PrepaidFlatUsageModel;
import com.juvarya.nivaas.customer.model.PrepaidMeterModel;
import com.juvarya.nivaas.customer.model.SocietyDue;
import com.juvarya.nivaas.customer.model.constants.FlatPaymentStatus;
import com.juvarya.nivaas.customer.model.constants.NotificationType;
import com.juvarya.nivaas.customer.populator.JsonDataConverter;
import com.juvarya.nivaas.customer.repository.MaintenanceRepository;
import com.juvarya.nivaas.customer.repository.PrepaidFlatUsageRepository;
import com.juvarya.nivaas.customer.repository.PrepaidMeterRepository;
import com.juvarya.nivaas.customer.repository.SocietyDueRepository;
import com.juvarya.nivaas.customer.service.NivaasFlatService;
import com.juvarya.nivaas.customer.service.NotificationService;
import com.juvarya.nivaas.customer.service.SocietyDueService;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@Service
@Slf4j
public class SocietyDueServiceImpl extends JTBaseEndpoint implements SocietyDueService {

	@Autowired
	private SocietyDueRepository societyDueRepository;

	@Autowired
	private PrepaidFlatUsageRepository prepaidFlatUsageRepository;

	@Autowired
	private NotificationService notificationService;

	@Autowired
	private NivaasFlatService flatService;

	@Autowired
	private NotificationPublisher notificationPublisher;

	@Autowired
	private MaintenanceRepository maintenanceRepository;

	@Autowired
	private PrepaidMeterRepository prepaidMeterRepository;

	@Override
	@Transactional
	public void saveAndNotifySocietyDue(final List<PrepaidMeterModel> prepaidMeterModels, final Long apartmentId,
			final Double fixedCost) {
		log.info("Saving and notifying Society Due for apartment {}", apartmentId);
		List<NivaasFlatModel> flatModelList = flatService.getAllFlatsByApartment(apartmentId);
		flatModelList.forEach(flatModel -> {
			final List<PrepaidFlatUsageModel> prepaidFlatUsageModels = prepaidFlatUsageRepository
					.findByFlatIdAndApartmentId(flatModel.getId(), apartmentId);
			final List<FlatUsageDto> flatUsageDtoList;
			final double totalCost;
			if (CollectionUtils.isEmpty(prepaidMeterModels)) {
				flatUsageDtoList = new ArrayList<>();
				totalCost = fixedCost;
			} else {
				flatUsageDtoList = prepareFlatUsageDtoForAllPrepaidMeters(prepaidFlatUsageModels, prepaidMeterModels);
				totalCost = calculateTotalCost(fixedCost, flatUsageDtoList);
			}
			SocietyDue societyDue = buildSocietyDue(apartmentId, flatModel.getId(), totalCost,
					new JsonDataConverter().convertToDatabaseColumn(flatUsageDtoList));
			societyDueRepository.save(societyDue);

			// save notification
			NotificationModel notificationModel = createNotificationModel(flatModel, totalCost, societyDue);

			notificationService.save(notificationModel);

			String status = FlatPaymentStatus.UNPAID.name();

			if (null != flatModel.getOwnerId()) {
				notificationPublisher.sendNotification(apartmentId, flatModel.getOwnerId(), false, false,
						flatModel.getId(), false, false, societyDue.getId(), status, totalCost, true, null);
			} else if (null != flatModel.getTenantId()) {
				notificationPublisher.sendNotification(apartmentId, flatModel.getTenantId(), false, false,
						flatModel.getId(), false, false, societyDue.getId(), status, totalCost, true, null);
			}

		});
	}

	@Override
	public void saveAndNotifySocietyDueByUserAdmin(final Long apartmentId) {
		log.info("Saving and notifying Society Due by User Admin for apartment {}", apartmentId);
		Optional<MaintenanceModel> maintenanceModel = maintenanceRepository.findByApartmentId(apartmentId);
		maintenanceModel.ifPresent(jtMaintananceModel -> saveAndNotifySocietyDue(jtMaintananceModel.getMeters(),
				apartmentId, jtMaintananceModel.getCost()));
	}

	private List<FlatUsageDto> prepareFlatUsageDtoForAllPrepaidMeters(
			final List<PrepaidFlatUsageModel> prepaidFlatUsageModels,
			final List<PrepaidMeterModel> prepaidMeterModels) {
		log.debug("Preparing Flat Usage DTOs for prepaid meters");
		final List<FlatUsageDto> flatUsageDtoList = new ArrayList<>();
		final Map<Long, PrepaidFlatUsageModel> usageMap = prepaidFlatUsageModels.stream()
				.collect(Collectors.toMap(PrepaidFlatUsageModel::getPrepaidMeterId,
						prepaidFlatUsageModel -> prepaidFlatUsageModel, (existing, replacement) -> replacement // override
																												// if
																												// multiple
																												// entries
				));
		prepaidMeterModels.forEach(prepaidMeterModel -> {
			PrepaidFlatUsageModel usageModel = usageMap.get(prepaidMeterModel.getId());
			if (usageModel != null) {
				FlatUsageDto flatUsageDto = createFlatUsageDto(prepaidMeterModel.getId(), prepaidMeterModel.getName(),
						prepaidMeterModel.getCostPerUnit(), usageModel.getUnitsConsumed());
				flatUsageDtoList.add(flatUsageDto);
			} else {
				log.warn("No usage data found for prepaid meter ID: {}", prepaidMeterModel.getId());
			}
		});
		return flatUsageDtoList;
	}

	private static double calculateTotalCost(double fixedCost, List<FlatUsageDto> flatUsageDtos) {
		log.debug("Calculating total cost for flat usage");
		double variableCost = flatUsageDtos.stream().filter(Objects::nonNull)
				.mapToDouble(dto -> dto.getCostPerUnit() * dto.getUnitsConsumed()).sum();

		return fixedCost + variableCost;
	}

	private static FlatUsageDto createFlatUsageDto(final Long prepaidMeterId, final String prepaidName,
			final Double costPerUnit, final Double noOfUnits) {
		log.debug("Creating FlatUsageDto for prepaid meter {}", prepaidMeterId);
		FlatUsageDto flatUsageDto = new FlatUsageDto();
		flatUsageDto.setCostPerUnit(costPerUnit);
		flatUsageDto.setPrepaidMeterId(prepaidMeterId);
		flatUsageDto.setName(prepaidName);
		flatUsageDto.setUnitsConsumed(noOfUnits);
		return flatUsageDto;
	}

	private static SocietyDue buildSocietyDue(final Long apartmentId, final Long flatId, final Double totalAmount,
			final String maintenanceDetails) {
		log.debug("Building SocietyDue object");
		SocietyDue societyDue = new SocietyDue();
		societyDue.setApartmentId(apartmentId);
		societyDue.setFlatId(flatId);
		societyDue.setDueDate(LocalDate.now());
		societyDue.setStatus(FlatPaymentStatus.UNPAID);
		societyDue.setCost(totalAmount);
		societyDue.setMaintenanceDetails(maintenanceDetails);
		return societyDue;
	}

	private static NotificationModel createNotificationModel(final NivaasFlatModel flatModel, final Double totalCost,
			final SocietyDue societyDue) {
		log.debug("Creating NotificationModel");
		NotificationModel notificationModel = new NotificationModel();
		notificationModel.setFlatModel(flatModel);
		notificationModel.setUserId(flatModel.getOwnerId());
		notificationModel.setTenantId(flatModel.getTenantId());
		notificationModel.setSocietyDue(societyDue);
		notificationModel.setMessage(flatModel.getApartment().getName() + " " + flatModel.getFlatNo() + " "
				+ flatModel.getOwnerId() + " " + ", you have pending dues. Please pay the" + " " + totalCost + " "
				+ "for your flat:" + " " + flatModel.getFlatNo());
		notificationModel.setType(NotificationType.SOCIETY_DUE);
		return notificationModel;
	}

	@Override
	public SocietyDue findById(Long id) {
		log.info("Fetching SocietyDue by ID: {}", id);
		Optional<SocietyDue> society = societyDueRepository.findById(id);
		return society.orElse(null);
	}

	public Optional<SocietyDue> getSocietyDues(Long apartmentId, Long flatId, int year, int month) {
		log.info("Fetching SocietyDue for apartment {} and flat {} for year {} and month {}", apartmentId, flatId, year,
				month);
		return societyDueRepository.getSocietyDues(apartmentId, flatId, year, month);
	}

	@Override
	public List<SocietyDTO> getAllSocietyDues(Long apartmentId, int year, int month) {
		log.info("Fetching all SocietyDues for apartment {} for year {} and month {}", apartmentId, year, month);
		List<SocietyDue> dues = societyDueRepository.getAllSocietyDues(apartmentId, year, month);

		List<SocietyDTO> societyDues = dues.stream().map(due -> {
			NivaasFlatModel flatModel = flatService.findById(due.getFlatId());
			return SocietyDTO.builder().id(due.getId()).flatId(due.getFlatId()).cost(due.getCost())
					.status(due.getStatus()).flatNo(flatModel.getFlatNo()) // Assuming mapToFlatDetailsDTO method maps
																			// Flat to FlatDetailsDTO
					.build();
		}).collect(Collectors.toList());

		return societyDues;
	}

	@Override
	public void updateStatus(Long apartmentId, String status, List<Long> ids) {
		List<SocietyDue> societyDues = societyDueRepository.findByApartmentId(apartmentId);
		societyDues.forEach(dues -> {
			ids.forEach(id -> {
				Optional<SocietyDue> societyDue = societyDueRepository.findById(id);
				societyDue.ifPresent(due -> {
					due.setStatus(updateStatus(status));
					societyDueRepository.save(due);
				});
			});
		});
	}

	@SuppressWarnings("unlikely-arg-type")
	private FlatPaymentStatus updateStatus(String status) {
		if (FlatPaymentStatus.PAID.name().equalsIgnoreCase(status)) {
			return FlatPaymentStatus.PAID;
		} else {
			return FlatPaymentStatus.UNPAID;
		}
	}

}
