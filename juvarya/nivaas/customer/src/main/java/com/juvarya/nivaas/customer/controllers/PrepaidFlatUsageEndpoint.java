package com.juvarya.nivaas.customer.controllers;

import javax.validation.Valid;

import com.juvarya.nivaas.customer.dto.request.PrepaidConsumptionDto;
import com.juvarya.nivaas.utils.NivaasConstants;

import lombok.extern.slf4j.Slf4j;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.customer.service.PrepaidFlatUsageService;

@RestController
@RequestMapping(value = "/prepaid-usage")
@Slf4j
public class PrepaidFlatUsageEndpoint {
	@Autowired
	private PrepaidFlatUsageService prepaidFlatUsageService;

	@SuppressWarnings("rawtypes")
	@PostMapping("/flat/update-consumed")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity updateConsumed(@RequestBody @Valid PrepaidConsumptionDto prepaidConsumptionDto) {
		log.info("Consumed amount updated successfully for prepaid flat with DTO: {}", prepaidConsumptionDto);
		return prepaidFlatUsageService.updateConsumed(prepaidConsumptionDto);
	}

	@SuppressWarnings("rawtypes")
	@GetMapping("/apartment/{apartmentId}/prepaid/{prepaidId}/list")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity getFlatPrepaidList(@Valid @PathVariable Long apartmentId, @PathVariable Long prepaidId)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Fetched prepaid usage list successfully for apartmentId: {} and prepaidId: {}", apartmentId, prepaidId);
		return prepaidFlatUsageService.getFlatUsage(apartmentId, prepaidId);
	}

}
