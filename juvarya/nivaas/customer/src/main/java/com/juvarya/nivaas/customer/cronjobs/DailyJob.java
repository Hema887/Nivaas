package com.juvarya.nivaas.customer.cronjobs;

import java.time.LocalDate;
import java.time.YearMonth;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import com.juvarya.nivaas.customer.model.MaintenanceModel;
import com.juvarya.nivaas.customer.repository.MaintenanceRepository;
import com.juvarya.nivaas.customer.repository.PrepaidFlatUsageRepository;
import com.juvarya.nivaas.customer.service.SocietyDueService;

@Component
@Slf4j
public class DailyJob {

	@Autowired
	private MaintenanceRepository maintenanceRepository;

	@Autowired
	private PrepaidFlatUsageRepository prePaidFlatMeterRepository;

	@Autowired
	private SocietyDueService societyDueService;

	@Scheduled(cron = "0 0 4 * * *")
	@Transactional
	public void getNotifications() {
		LocalDate currentDate = LocalDate.now();
		int dayOfMonth = currentDate.getDayOfMonth();
		log.info("Daily job started on {}", dayOfMonth);
		List<Integer> notificationDays = getNotificationDays(currentDate, dayOfMonth);
		List<MaintenanceModel> jtMaintenanceModels = maintenanceRepository.findByNotifyOnIn(notificationDays);

		if (!CollectionUtils.isEmpty(jtMaintenanceModels)) {
			log.info("Number of apartments scheduled for today {}", jtMaintenanceModels.size());
			jtMaintenanceModels.forEach(maintenanceModel -> societyDueService.saveAndNotifySocietyDue(maintenanceModel.getMeters(),
					maintenanceModel.getApartmentModel().getId(), maintenanceModel.getCost()));
		} else {
			log.info("No Maintenances scheduled for today");
		}
	}

	private List<Integer> getNotificationDays(LocalDate currentDate, int dayOfMonth) {
		YearMonth yearMonth = YearMonth.from(currentDate);
		int lastDayOfMonth = yearMonth.lengthOfMonth();
		List<Integer> notificationDays = new ArrayList<>();

		notificationDays.add(dayOfMonth); // Always include the current day

		if (dayOfMonth == lastDayOfMonth) {
			if (lastDayOfMonth == 28) {
				// For February in non-leap years
				notificationDays.addAll(Arrays.asList(29, 30, 31));
			} else if (lastDayOfMonth == 29) {
				// For February in leap years
				notificationDays.addAll(Arrays.asList(30, 31));
			} else if (lastDayOfMonth == 30) {
				// For months with 30 days
				notificationDays.add(31);
			}
		}

		return notificationDays;
	}

}
