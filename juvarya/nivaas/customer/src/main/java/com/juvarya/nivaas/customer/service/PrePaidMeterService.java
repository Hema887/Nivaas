package com.juvarya.nivaas.customer.service;

import org.springframework.http.ResponseEntity;

import com.juvarya.nivaas.customer.dto.JTPrePaidMeterDTO;

@SuppressWarnings("rawtypes")
public interface PrePaidMeterService {

	ResponseEntity save(JTPrePaidMeterDTO jtPrePaidDTO);

	ResponseEntity findById(Long id) throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	ResponseEntity delete(Long id);

	ResponseEntity getPrePaidMeterList(Long apartmentId, int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;
	
	ResponseEntity updatePrePaidMeter(JTPrePaidMeterDTO jtPrePaidMeterDTO);
}
