package com.juvarya.nivaas.customer.service.impl;

import com.juvarya.nivaas.customer.dto.ApartmentDebitDto;
import com.juvarya.nivaas.customer.model.ApartmentDebitHistoryModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.repository.ApartmentDebitHistoryRepository;
import com.juvarya.nivaas.customer.service.ApartmentDebitHistoryService;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Optional;

@Service
@Transactional
@Slf4j
public class ApartmentDebitHistoryServiceImpl implements ApartmentDebitHistoryService {

    @Autowired
    private ApartmentDebitHistoryRepository apartmentDebitHistoryRepository;

    @Override
    public ApartmentDebitHistoryModel addDebitHistory(final ApartmentDebitDto debitHistory, final NivaasApartmentModel nivaasApartmentModel,
                                                      final Long userId) {
    	log.info("Adding debit history for apartment: {}, user: {}", nivaasApartmentModel.getId(), userId);
        ApartmentDebitHistoryModel apartmentDebitHistoryModel = ApartmentDebitHistoryModel.converter(debitHistory);
        apartmentDebitHistoryModel.setApartmentModel(nivaasApartmentModel);
        apartmentDebitHistoryModel.setUpdatedBy(userId);
        return apartmentDebitHistoryRepository.save(apartmentDebitHistoryModel);
    }

    @Override
    @Modifying
    public void updateDebitHistory(final Long id, final ApartmentDebitDto debitHistory) {
    	 log.info("Updating debit history with id: {}", id);
        Optional<ApartmentDebitHistoryModel> apartmentDebitHistoryModel = apartmentDebitHistoryRepository.findById(id);
        if (apartmentDebitHistoryModel.isPresent()) {
            apartmentDebitHistoryModel.map(apartmentDebitHistory -> {
                apartmentDebitHistory.setTransactionDate(debitHistory.getTransactionDate());
                apartmentDebitHistory.setType(debitHistory.getType());
                apartmentDebitHistory.setDescription(debitHistory.getDescription());
                apartmentDebitHistory.setAmount(debitHistory.getAmount());
                apartmentDebitHistory.setUpdatedAt(new Date());
                return apartmentDebitHistoryRepository.save(apartmentDebitHistory);
            });
        }
    }

    @Override
    public boolean deleteDebitHistory(final Long id) {
    	log.info("Deleting debit history with id: {}", id);
        return apartmentDebitHistoryRepository.findById(id)
                .map(debitHistory -> {
                    apartmentDebitHistoryRepository.delete(debitHistory);
                    return true;
                }).orElse(false);
    }

    @Override
    public List<ApartmentDebitHistoryModel> getAllDebitHistories(final Long apartmentId, final int year, final int month) {
        return apartmentDebitHistoryRepository.findByApartmentIdAndYearAndMonth(apartmentId, year, month);
    }

    public Optional<ApartmentDebitHistoryModel> getDebitHistoryById(final Long id) {
        return apartmentDebitHistoryRepository.findById(id);
    }

}
