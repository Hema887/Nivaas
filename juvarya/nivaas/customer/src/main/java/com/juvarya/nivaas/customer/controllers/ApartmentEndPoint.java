package com.juvarya.nivaas.customer.controllers;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.ErrorResponse;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.commonservice.dto.ApartmentDTO;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.customer.dto.UpdateApartmentDTO;
import com.juvarya.nivaas.customer.dto.request.ApartmentCoAdminDTO;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.populator.ApartmentPopulator;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.service.ApartmentUserRoleService;
import com.juvarya.nivaas.customer.service.impl.NivaasApartmentServiceImpl;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.SecurityUtils;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/jtapartment")
@Slf4j
public class ApartmentEndPoint extends JTBaseEndpoint {

	@Autowired
	private NivaasApartmentService nivaasApartmentService;

	@Autowired
	private ApartmentPopulator apartmentPopulator;

	@Autowired
	private ApartmentUserRoleService apartmentUserRoleService;

	@PostMapping("/add/co-admin")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity addCoAdmin(@Valid @RequestBody ApartmentCoAdminDTO coAdminDTO) {
		log.info("Adding co-admin with User ID: {}", coAdminDTO.getUserId());
		if (ERole.ROLE_APARTMENT_ADMIN == coAdminDTO.getUserRole()) {
			nivaasApartmentService.addCoAdmin(coAdminDTO);

			log.info("Onboarding CoAdmin request sent for User ID: {}", coAdminDTO.getUserId());
			return ResponseEntity.ok().body(new MessageResponse("OnBoarding CoAdmin request sent"));
		}
		log.warn("Invalid user role: {}", coAdminDTO.getUserRole());
		return ResponseEntity.badRequest().build();
	}

	@PostMapping("/save")
	public ResponseEntity save(@Valid @RequestBody ApartmentDTO apartmentDTO) {
		try {
			UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();

			log.info("Saving apartment: {}", apartmentDTO.getName());
			nivaasApartmentService.sendOnBoardRequestForApartment(apartmentDTO, user.getId());
			log.info("OnBoarding Request Sent for apartment: {}", apartmentDTO.getName());
			return ResponseEntity.ok().body(new MessageResponse("OnBoarding Request Sent"));

		} catch (NivaasCustomerException e) {
			ErrorCode errorCode = e.getErrorCode();
			ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), errorCode.getMessage());
			return ResponseEntity.status(errorCode.getHttpStatus()).body(errorResponse);
		}

	}

	@SuppressWarnings("unchecked")
	@GetMapping("/{id}")
	public ResponseEntity getById(@PathVariable("id") Long id)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Getting apartment by ID: {}", id);
		NivaasApartmentModel nivaasApartmentModel = nivaasApartmentService.findById(id);
		if (Objects.nonNull(nivaasApartmentModel)) {
			ApartmentDTO apartmentDTO = (ApartmentDTO) getConverterInstance().convert(nivaasApartmentModel);
			log.info("Apartment found: {}", apartmentDTO.getName());
			return ResponseEntity.ok().body(apartmentDTO);
		}
		log.warn("Apartment not found with ID: {}", id);
		throw new NivaasCustomerException(ErrorCode.APARTMENT_NOT_FOUND);
	}

	@DeleteMapping("/delete/{apartmentId}")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity deleteApartment(@PathVariable("apartmentId") Long apartmentId) {
		log.info("Deleting apartment with ID: {}", apartmentId);
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		NivaasApartmentModel nivaasApartmentModel = nivaasApartmentService.findById(apartmentId);

		if (Objects.isNull(nivaasApartmentModel)) {
			log.warn("Apartment not found with ID: {}", apartmentId);
			throw new NivaasCustomerException(ErrorCode.APARTMENT_NOT_FOUND);
		}

		ApartmentUserRoleModel apartmentUserRoleModel = apartmentUserRoleService
				.findByApartmentModelAndJtCustomer(nivaasApartmentModel, loggedInUser.getId());

		if (null != apartmentUserRoleModel
				&& apartmentUserRoleModel.getRoleName().equals(ERole.ROLE_APARTMENT_ADMIN.name())) {
			nivaasApartmentService.removeApartment(nivaasApartmentModel);
			log.info("Apartment deleted with ID: {}", apartmentId);
			return ResponseEntity.ok().body(new MessageResponse("Apartment Deleted"));
		}
		log.warn("User not allowed to remove apartment with ID: {}", apartmentId);
		throw new NivaasCustomerException(ErrorCode.APARTMENT_NOT_ALLOWED);
	}

	@SuppressWarnings("unchecked")
	@PutMapping("/update")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity update(@Valid @RequestBody UpdateApartmentDTO updateApartmentDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Updating apartment with ID: {}", updateApartmentDTO.getId());
		UserDetailsImpl userDetails = SecurityUtils.getCurrentUserDetails();

		NivaasApartmentModel jtApartment = nivaasApartmentService.findById(updateApartmentDTO.getId());
		if (Objects.isNull(jtApartment)) {
			log.warn("Apartment Not found");
			throw new NivaasCustomerException(ErrorCode.APARTMENT_NOT_FOUND);
		}

		ApartmentUserRoleModel apartmentUserRole = apartmentUserRoleService
				.findByApartmentModelAndJtCustomer(jtApartment, userDetails.getId());

		if (Objects.isNull(apartmentUserRole)) {
			log.warn("You Are Not Able To Update Apartment Details");
			throw new NivaasCustomerException(ErrorCode.APARTMENT_UPDATE_NOT_ALLOWED);
		}

		if (Objects.nonNull(updateApartmentDTO.getApartmentType())) {
			jtApartment.setApartmentType(NivaasApartmentServiceImpl.apartmentType(updateApartmentDTO.getApartmentType()));
		}
		if (Objects.nonNull(updateApartmentDTO.getBuilderName())) {
			jtApartment.setBuilderName(updateApartmentDTO.getBuilderName());
		}
		if (Objects.nonNull(updateApartmentDTO.getCode())) {
			jtApartment.setCode(updateApartmentDTO.getCode());
		}
		if (Objects.nonNull(updateApartmentDTO.getDescription())) {
			jtApartment.setDescription(updateApartmentDTO.getDescription());
		}
		if (Objects.nonNull(updateApartmentDTO.getName())) {
			jtApartment.setName(updateApartmentDTO.getName());
		}
		if (updateApartmentDTO.getTotalFlats() != 0) {
			jtApartment.setTotalFlats(updateApartmentDTO.getTotalFlats());
		}
		jtApartment = nivaasApartmentService.saveApartment(jtApartment);
		ApartmentDTO jtapartmentDTO = (ApartmentDTO) getConverterInstance().convert(jtApartment);
		log.info("Apartment updated: {}", jtapartmentDTO.getName());
		return ResponseEntity.ok().body(jtapartmentDTO);
	}

	@GetMapping("/getByName")
	public ResponseEntity findByApartmentName(@Valid @RequestParam String name)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Finding apartments by name: {}", name);
		List<ApartmentDTO> dto = nivaasApartmentService.findbyApartmentName(name);
		if (Objects.nonNull(dto)) {
			log.info("Found {} apartments with name: {}", dto.size(), name);
			return ResponseEntity.ok().body(dto);
		}
		log.warn("No apartments found with name: {}", name);
		throw new NivaasCustomerException(ErrorCode.APARTMENT_NOT_FOUND);
	}

	@GetMapping("/nearbyapartments")
	public Map<String, Object> getApartmentsByCity(@RequestParam Long cityId, @RequestParam int pageNo,
			@RequestParam int pageSize) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return nivaasApartmentService.nearyByApartments(cityId, pageNo, pageSize);
	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		log.debug("Getting converter instance");
		return getConverter(apartmentPopulator, ApartmentDTO.class.getName());
	}
}
