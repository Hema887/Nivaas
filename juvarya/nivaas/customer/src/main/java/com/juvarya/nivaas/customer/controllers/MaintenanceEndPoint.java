package com.juvarya.nivaas.customer.controllers;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.customer.dto.JTMaintenanceDTO;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.PrepaidMeterModel;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.MaintenanceService;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/jtmaintanance")
@Slf4j
public class MaintenanceEndPoint extends JTBaseEndpoint {

	@Autowired
	private MaintenanceService maintenanceService;

	@GetMapping("/getPrepaid")
	public ResponseEntity getPrepaid(@Valid @RequestParam NivaasApartmentModel apartment) {
		log.info("Entering getPrepaid API with apartment: {}", apartment);
		List<PrepaidMeterModel> prepaidList = maintenanceService.getPrepaid(apartment);
		log.info("Retrieved {} prepaid meters for apartment: {}", prepaidList.size(), apartment);
		return ResponseEntity.ok().body(prepaidList);
	}

	@PostMapping("/save")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity save(@RequestBody @Valid JTMaintenanceDTO jtMaintenanceDTO) {
		log.info("Maintenance record creation response: {}", jtMaintenanceDTO);
		return maintenanceService.create(jtMaintenanceDTO);
	}

	@GetMapping("/{apartmentId}")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity getMaintenanceByApartment(@PathVariable("apartmentId") Long apartmentId)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		JTMaintenanceDTO jtMaintenanceDTO = maintenanceService.findByApartment(apartmentId);
		if (Objects.isNull(jtMaintenanceDTO)) {
			return ResponseEntity.ok().body(new MessageResponse("Maitenance Not Found"));
		}
		return ResponseEntity.ok().body(jtMaintenanceDTO);
	}

}
