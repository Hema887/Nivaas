package com.juvarya.nivaas.customer.controllers;

import java.util.List;
import java.util.Optional;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.customer.dto.SocietyDTO;
import com.juvarya.nivaas.customer.model.SocietyDue;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.SocietyDueService;
import com.juvarya.nivaas.utils.NivaasConstants;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping(value = "/society")
@Slf4j
public class SocietyEndpoint {

	@Autowired
	private SocietyDueService societyDueService;

	@GetMapping("/dues/apartment/{apartmentId}/flat/{flatId}")
	@PreAuthorize(NivaasConstants.ROLE_FLAT_OWNER + " " + NivaasConstants.OR + " " + NivaasConstants.ROLE_FLAT_TENANT)
	public Optional<SocietyDue> getSocietyDues(@PathVariable Long apartmentId, @PathVariable Long flatId,
			@RequestParam @Min(2024) int year, @RequestParam @Min(1) @Max(12) int month)
	{
		log.info("Request received to fetch society dues for Apartment ID: {}, Flat ID: {}, Year: {}, Month: {}",
				apartmentId, flatId, year, month);
		return societyDueService.getSocietyDues(apartmentId, flatId, year, month);
	}

	@GetMapping("/dues/list/{apartmentId}/{year}/{month}")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity<List<SocietyDTO>> getAllSocietyDues(@RequestParam int pageNo, @RequestParam int pageSize,
			@PathVariable Long apartmentId, @PathVariable int year, @PathVariable int month)
	{
		log.info("Request received to fetch all society dues for Apartment ID: {}, Year: {}, Month: {}", apartmentId,
				year, month);
		List<SocietyDTO> societyDuesDTO = societyDueService.getAllSocietyDues(apartmentId, year, month);
		return ResponseEntity.ok(societyDuesDTO);
	}

	@SuppressWarnings("rawtypes")
	@GetMapping("/dues/trigger/apartment/{apartmentId}")
	@PreAuthorize(NivaasConstants.ROLE_USER_ADMIN)
	public ResponseEntity triggerSocietyDueForAnApartment(@PathVariable Long apartmentId)
	{
		log.info("Request received to trigger society dues for Apartment ID: {}", apartmentId);
		societyDueService.saveAndNotifySocietyDueByUserAdmin(apartmentId);
		return ResponseEntity.status(HttpStatus.ACCEPTED)
				.body(new MessageResponse("Triggered society dues for apartment " + apartmentId));
	}

	@PutMapping("/update")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity updateSocietyDueStatus(@RequestParam Long apartmentId, @RequestParam String status,
			@RequestParam List<Long> societyDueIds) {
		 societyDueService.updateStatus(apartmentId, status, societyDueIds);
		 return ResponseEntity.ok().body(new MessageResponse("updated SocietyDue"));
	}

}
