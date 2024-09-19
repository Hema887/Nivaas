package com.juvarya.nivaas.customer.controllers;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.customer.dto.JTPrePaidMeterDTO;
import com.juvarya.nivaas.customer.service.PrePaidMeterService;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@RestController
@RequestMapping(value = "/prepaidmeter")
@Slf4j
public class PrePaidMeterEndpoint extends JTBaseEndpoint {

	@Autowired
	private PrePaidMeterService prePaidMeterService;

	@PostMapping("/save")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity savePrepaidMeter(@Valid @RequestBody JTPrePaidMeterDTO jtPrePaidMeterDTO) {
		log.info("Prepaid meter saved successfully with DTO: {}", jtPrePaidMeterDTO);
		return prePaidMeterService.save(jtPrePaidMeterDTO);
	}

	@GetMapping("/{id}")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity findById(@PathVariable("id") Long id)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		   log.info("Found prepaid meter with id: {}", id);
		return prePaidMeterService.findById(id);
	}

	@DeleteMapping("/delete")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity deletePrePaidMeter(@Valid @RequestParam Long prePaidId) {
		log.info("Prepaid meter deleted successfully with id: {}", prePaidId);
		return prePaidMeterService.delete(prePaidId);
	}

	@GetMapping("/list")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity getPrePaidMeterList(@Valid @RequestParam Long apartmentId, @RequestParam int pageNo,
			@RequestParam int pageSize) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		 log.info("Fetched prepaid meter list successfully for apartmentId: {}, page: {}, pageSize: {}", apartmentId, pageNo, pageSize);
		return prePaidMeterService.getPrePaidMeterList(apartmentId, pageNo, pageSize);
	}

	@PutMapping("/update")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity updatePreapidMeter(@Valid @RequestBody JTPrePaidMeterDTO jtPrePaidMeterDTO) {
		 log.info("Prepaid meter updated successfully with DTO: {}", jtPrePaidMeterDTO);
		return prePaidMeterService.updatePrePaidMeter(jtPrePaidMeterDTO);
	}
}
