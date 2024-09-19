package com.juvarya.nivaas.customer.service;

import java.util.List;
import java.util.Optional;

import com.juvarya.nivaas.customer.dto.ApartmentCreditDto;
import com.juvarya.nivaas.customer.model.ApartmentCreditHistoryModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;

public interface ApartmentCreditHistoryService {
	
	ApartmentCreditHistoryModel addCreditHistory(final ApartmentCreditDto creditHistory, final NivaasApartmentModel apartmentModel, final Long userId);
	
	void updateCreditHistory(final Long id, final ApartmentCreditDto creditHistory);
	
	boolean deleteCreditHistory(final Long id);
	
	List<ApartmentCreditHistoryModel> getAllCreditHistories(final Long apartemntId, final int year, final int month);
	
	Optional<ApartmentCreditHistoryModel> getCreditHistoryById(final Long id);
}
