package com.juvarya.nivaas.customer.service;

import java.util.List;
import java.util.Optional;

import com.juvarya.nivaas.customer.dto.SocietyDTO;
import com.juvarya.nivaas.customer.model.PrepaidMeterModel;
import com.juvarya.nivaas.customer.model.SocietyDue;

public interface SocietyDueService {

	Optional<SocietyDue> getSocietyDues(final Long apartmentModel, final Long flatModel, final int year,
			final int month);

	List<SocietyDTO> getAllSocietyDues(Long apartmentId, int year, int month);

	void saveAndNotifySocietyDue(final List<PrepaidMeterModel> prepaidMeterModels, final Long apartmentId,
			final Double fixedCost);

	SocietyDue findById(Long id);

	void saveAndNotifySocietyDueByUserAdmin(final Long apartmentId);

	void updateStatus(Long apartmentId, String status, List<Long> ids);
}
