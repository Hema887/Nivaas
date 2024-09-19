package com.juvarya.nivaas.customer.controllers;

import java.util.List;
import java.util.Objects;

import javax.validation.Valid;
import javax.validation.constraints.Max;
import javax.validation.constraints.Min;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.customer.dto.ApartmentCreditDto;
import com.juvarya.nivaas.customer.dto.MessageDTO;
import com.juvarya.nivaas.customer.model.ApartmentCreditHistoryModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.service.ApartmentCreditHistoryService;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.util.UserRoleHelper;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.SecurityUtils;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping(value = "/apartment/credit-history")
@Slf4j
public class ApartmentCreditHistoryEndpoint {

	@Autowired
	private ApartmentCreditHistoryService apartmentCreditHistoryService;

	@Autowired
	private UserRoleHelper userRoleHelper;

	@Autowired
	private NivaasApartmentService apartmentService;

	@PostMapping("/save")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity addCreditHistory(@RequestBody @Valid ApartmentCreditDto creditHistory) {
		UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();
		log.info("Received request to add credit history: {}", creditHistory);
		NivaasApartmentModel nivaasApartmentModel = apartmentService.findById(creditHistory.getApartmentId());
		if (Objects.isNull(nivaasApartmentModel)
				|| !userRoleHelper.isValidApartmentAdmin(user.getId(), nivaasApartmentModel)) {
			log.warn("Unauthorized access or invalid apartment for user: {}", user.getPrimaryContact());

			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		ApartmentCreditHistoryModel savedCreditHistory = apartmentCreditHistoryService.addCreditHistory(creditHistory,
				nivaasApartmentModel, user.getId());
		log.info("Credit history added successfully for apartmentId: {} by user: {}", creditHistory.getApartmentId(),
				user.getId());

		return ResponseEntity.status(201).body(savedCreditHistory);
	}

	@PutMapping("/{id}")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity updateCreditHistory(@PathVariable Long id,
			@RequestBody @Valid ApartmentCreditDto updatedCreditHistory) {
		UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();
		log.info("Received request to update debit history with id: {} by user: {}", id, user);

		NivaasApartmentModel nivaasApartmentModel = apartmentService.findById(updatedCreditHistory.getApartmentId());
		if (Objects.isNull(nivaasApartmentModel)
				|| !userRoleHelper.isValidApartmentAdmin(user.getId(), nivaasApartmentModel)) {
			log.warn("Unauthorized access or invalid apartment for user: {}", user);

			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		apartmentCreditHistoryService.updateCreditHistory(id, updatedCreditHistory);
		log.info("Debit history with id: {} updated successfully by user: {}", id, user);

		return ResponseEntity.ok().body(new MessageDTO("Updated the record"));
	}

	@DeleteMapping("/apartment/{apartmentId}/credit/{id}")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity<Void> deleteCreditHistory(@PathVariable Long apartmentId, @PathVariable Long id) {
		UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();
		log.info("Received request to delete Credit history with id: {} in apartment: {} by user: {}", id, apartmentId,
				user);
		NivaasApartmentModel nivaasApartmentModel = apartmentService.findById(apartmentId);
		if (Objects.isNull(nivaasApartmentModel)
				|| !userRoleHelper.isValidApartmentAdmin(user.getId(), nivaasApartmentModel)) {
			log.warn("Unauthorized access or invalid apartment for user: {}", user);

			return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
		}
		if (apartmentCreditHistoryService.deleteCreditHistory(id)) {
			log.info("Credit history with id: {} deleted successfully by user: {}", id, user);

			return ResponseEntity.ok().build();
		} else {
			log.info("Credit history with id: {} not found for deletion", id);

			return ResponseEntity.notFound().build();
		}
	}
	
	@GetMapping("/apartment/{apartmentId}/year/{year}/month/{month}")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN + " " + NivaasConstants.OR + " " + NivaasConstants.ROLE_FLAT_OWNER)
	public List<ApartmentCreditHistoryModel> getAllCreditHistories(@PathVariable Long apartmentId,
			@PathVariable @Valid int year, @PathVariable @Valid @Min(1) @Max(12) int month) {
		log.info("Fetching all credit histories for apartmentId: {} in year: {}, month: {}", apartmentId, year, month);
		return apartmentCreditHistoryService.getAllCreditHistories(apartmentId, year, month);
	}

	@GetMapping("/{id}")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN + " " + NivaasConstants.OR + " " + NivaasConstants.ROLE_FLAT_OWNER)
	public ResponseEntity<ApartmentCreditHistoryModel> getCreditHistoryById(@PathVariable Long id) {
		log.info("Fetching credit history with id: {} ", id);

		return apartmentCreditHistoryService.getCreditHistoryById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
	}

}
