package com.juvarya.nivaas.customer.service;

import com.juvarya.nivaas.customer.dto.request.PrepaidConsumptionDto;
import org.springframework.http.ResponseEntity;

import com.juvarya.nivaas.customer.model.PrepaidFlatUsageModel;

@SuppressWarnings("rawtypes")
public interface PrepaidFlatUsageService {

	ResponseEntity getFlatUsage(final Long apartmentId, final Long prepaidId)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	PrepaidFlatUsageModel save(PrepaidFlatUsageModel prePaidFlatMeterModel);

	ResponseEntity updateConsumed(PrepaidConsumptionDto prepaidConsumptionDto);
}
