package com.juvarya.nivaas.customer.service.impl;

import java.util.Date;
import java.util.List;
import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;

import com.juvarya.nivaas.customer.dto.ApartmentCreditDto;
import com.juvarya.nivaas.customer.model.ApartmentCreditHistoryModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.repository.ApartmentCreditHistoryRepository;
import com.juvarya.nivaas.customer.service.ApartmentCreditHistoryService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@Transactional
public class ApartmentCreditHistoryServiceImpl implements ApartmentCreditHistoryService {

	@Autowired
	private ApartmentCreditHistoryRepository apartmentCreditHistoryRepository;

	@Override
	public ApartmentCreditHistoryModel addCreditHistory(ApartmentCreditDto creditHistory,
			NivaasApartmentModel apartmentModel, Long userId) {
		log.info("Adding credit history for apartment: {}, user: {}", apartmentModel.getId(), userId);
		ApartmentCreditHistoryModel apartmentCreditHistoryModel = ApartmentCreditHistoryModel.converter(creditHistory);
		apartmentCreditHistoryModel.setApartmentModel(apartmentModel);
		apartmentCreditHistoryModel.setUpdatedBy(userId);
		return apartmentCreditHistoryRepository.save(apartmentCreditHistoryModel);
	}

	@Override
	@Modifying
	public void updateCreditHistory(Long id, ApartmentCreditDto creditHistory) {
		log.info("Updating Credit history with id: {}", id);
		Optional<ApartmentCreditHistoryModel> apartmentCreditHistoryModel = apartmentCreditHistoryRepository
				.findById(id);
		if (apartmentCreditHistoryModel.isPresent()) {
			apartmentCreditHistoryModel.map(apartmentCreditHistory -> {
				apartmentCreditHistory.setAmount(creditHistory.getAmount());
				apartmentCreditHistory.setCreditType(creditHistory.getCreditType());
				apartmentCreditHistory.setDescription(creditHistory.getDescription());
				apartmentCreditHistory.setTransactionDate(creditHistory.getTransactionDate());
				apartmentCreditHistory.setUpdatedAt(new Date());
				return apartmentCreditHistoryRepository.save(apartmentCreditHistory);
			});
		}

	}

	@Override
	public boolean deleteCreditHistory(Long id) {
		log.info("Deleting Credit history with id: {}", id);
		return apartmentCreditHistoryRepository.findById(id).map(creditHistory -> {
			apartmentCreditHistoryRepository.delete(creditHistory);
			return true;
		}).orElse(false);
	}

	@Override
	public List<ApartmentCreditHistoryModel> getAllCreditHistories(Long apartemntId, int year, int month) {
		return apartmentCreditHistoryRepository.findByApartmentIdAndYearAndMonth(apartemntId, year, month);
	}

	@Override
	public Optional<ApartmentCreditHistoryModel> getCreditHistoryById(Long id) {
		return apartmentCreditHistoryRepository.findById(id);
	}
}
