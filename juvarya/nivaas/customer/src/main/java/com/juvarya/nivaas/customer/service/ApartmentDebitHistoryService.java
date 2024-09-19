package com.juvarya.nivaas.customer.service;

import com.juvarya.nivaas.customer.dto.ApartmentDebitDto;
import com.juvarya.nivaas.customer.model.ApartmentDebitHistoryModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;

import java.util.List;
import java.util.Optional;

public interface ApartmentDebitHistoryService {

    ApartmentDebitHistoryModel addDebitHistory(final ApartmentDebitDto debitHistory, final NivaasApartmentModel nivaasApartmentModel, final Long userId);

    void updateDebitHistory(final Long id, final ApartmentDebitDto debitHistory);

    boolean deleteDebitHistory(final Long id);

    List<ApartmentDebitHistoryModel> getAllDebitHistories(final Long apartmentModel, final int year, final int month);

    Optional<ApartmentDebitHistoryModel> getDebitHistoryById(final Long id);
}
