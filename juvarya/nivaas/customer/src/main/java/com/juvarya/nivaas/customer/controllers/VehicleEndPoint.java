package com.juvarya.nivaas.customer.controllers;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import javax.validation.Valid;

import com.juvarya.nivaas.utils.SecurityUtils;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.CollectionUtils;
import org.springframework.util.ObjectUtils;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.customer.dto.VehicleDTO;
import com.juvarya.nivaas.customer.model.VehicleModel;
import com.juvarya.nivaas.customer.model.constants.VehicleType;
import com.juvarya.nivaas.customer.populator.VehiclePopulator;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.VehicleService;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.converter.AbstractConverter;
import com.juvarya.nivaas.utils.converter.JTBaseEndpoint;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/jtVehicle")
@Slf4j
public class VehicleEndPoint extends JTBaseEndpoint {

	private static final String User = "ROLE_USER";

	@Autowired
	private VehicleService vehicleService;

	@Autowired
	private VehiclePopulator vehiclePopulator;

	@SuppressWarnings("unchecked")
	@PostMapping("/save")
	public ResponseEntity addVehicle(@Valid @RequestBody VehicleDTO vehicleDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		 log.info("Adding vehicle with DTO: {}", vehicleDTO);
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();
		if (ObjectUtils.isEmpty(loggedInUser)) {
			return ResponseEntity.badRequest().body(new MessageResponse("user should login to add vehicle"));
		}

		VehicleModel vehicle = new VehicleModel();
		vehicle.setBrand(vehicleDTO.getBrand());
		vehicle.setColor(vehicleDTO.getColor());
		vehicle.setVehicleNumber(vehicleDTO.getVehicleNumber());
		vehicle.setVehicleType(vehicleType(vehicleDTO.getVehicleType()));
		vehicle.setCustomerId(loggedInUser.getId());

		vehicle = vehicleService.addVehicle(vehicle);
		VehicleDTO jtVehicleDTO = (VehicleDTO) getConverterInstance().convert(vehicle);
		 log.info("Vehicle added successfully with DTO: {}", vehicleDTO);
		return ResponseEntity.ok().body(jtVehicleDTO);

	}

	@SuppressWarnings("unchecked")
	public AbstractConverter getConverterInstance() {
		return getConverter(vehiclePopulator, VehicleDTO.class.getName());

	}

	public VehicleType vehicleType(String type) {
		if (type.equals(VehicleType.TwoWheeler.toString())) {
			return VehicleType.TwoWheeler;
		} else if (type.equals(VehicleType.ThreeWheeler.toString())) {
			return VehicleType.ThreeWheeler;
		} else {
			return VehicleType.FourWheeler;
		}
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/{id}")
	public ResponseEntity getById(@RequestParam Long vehicleId)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		 log.info("Fetching vehicle by id: {}", vehicleId);
		Optional<VehicleModel> vehicle = vehicleService.findById(vehicleId);
		if (vehicle.isPresent()) {
			VehicleModel vehicleModel = vehicle.get();
			VehicleDTO vehicleDTO = (VehicleDTO) getConverterInstance().convert(vehicleModel);
			log.info("Vehicle found with id: {}", vehicleId);
			return ResponseEntity.ok().body(vehicleDTO);
		} else {
			log.info("Vehicle not found with id: {}", vehicleId);
			return ResponseEntity.notFound().build();
		}
	}

	@SuppressWarnings("unchecked")
	@GetMapping("/list")
	public ResponseEntity getAll(@Valid @RequestParam int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		log.info("Fetching all vehicles. Page: {}, PageSize: {}", pageNo, pageSize);
		Pageable pageable = (Pageable) PageRequest.of(pageNo, pageSize);
		Page<VehicleModel> vehicleModel = vehicleService.findAllVehicles(pageable);

		Map<String, Object> response = new HashMap<>();

		response.put(NivaasConstants.TOTAL_PAGES, vehicleModel.getTotalPages());
		response.put(NivaasConstants.PAGE_NUM, pageNo);
		response.put(NivaasConstants.PAGE_SIZE, pageSize);

		if (!CollectionUtils.isEmpty(vehicleModel.getContent())) {
			response.put(NivaasConstants.PROFILES, getConverterInstance().convertAll(vehicleModel.getContent()));
			response.put(NivaasConstants.CURRENT_PAGE, vehicleModel.getNumber());
			response.put(NivaasConstants.TOTAL_ITEMS, vehicleModel.getTotalElements());
			log.info("Fetched {} vehicles", vehicleModel.getNumberOfElements());
			return new ResponseEntity<>(response, HttpStatus.OK);
		}

		return new ResponseEntity<>(response, HttpStatus.OK);
	}

	@DeleteMapping("/delete")
	public ResponseEntity deleteVehicle(@Valid @RequestParam Long vehicleId) {
		log.info("Deleting vehicle with id: {}", vehicleId);
		Optional<VehicleModel> vehicleModel = vehicleService.findById(vehicleId);

		if (null != vehicleModel) {
			vehicleService.removeVehicle(vehicleId);
			log.info("Vehicle deleted successfully with id: {}", vehicleId);
			return ResponseEntity.ok().body(new MessageResponse("Vehicle Deleted"));
		}
		 log.info("Vehicle not found with id: {}", vehicleId);
		return ResponseEntity.ok().body(new MessageResponse("Vehicle Not Found With Given Id"));

	}

	@SuppressWarnings("unchecked")
	@PutMapping("/update")
	public ResponseEntity update(@Valid @RequestBody VehicleDTO vehicleDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		 log.info("Updating vehicle with DTO: {}", vehicleDTO);

		UserDetailsImpl user = SecurityUtils.getCurrentUserDetails();

		if (null == vehicleDTO.getId()) {
			log.info("Vehicle ID is null");
			return ResponseEntity.ok().body(new MessageResponse("vehicleId shouldn't be null"));
		}

		Optional<VehicleModel> vehicleModel = vehicleService.findById(vehicleDTO.getId());
		if (vehicleModel.isEmpty()) {
			log.info("Vehicle not found with ID: {}", vehicleDTO.getId());
			return ResponseEntity.ok().body(new MessageResponse("Vehicle not found"));
		}
		if (user.getId().equals(vehicleModel.get().getCustomerId())) {
			vehicleModel.get().setBrand(vehicleDTO.getBrand());
			vehicleModel.get().setColor(vehicleDTO.getColor());
			vehicleModel.get().setVehicleType(vehicleType(vehicleDTO.getVehicleType()));
			vehicleModel.get().setVehicleNumber(vehicleDTO.getVehicleNumber());

			VehicleModel jtVehicleModel = vehicleService.addVehicle(vehicleModel.get());
			VehicleDTO jtVehicleDTO = (VehicleDTO) getConverterInstance().convert(jtVehicleModel);
			 log.info("Vehicle updated successfully with DTO: {}", vehicleDTO);
			return ResponseEntity.ok().body(jtVehicleDTO);
		}
		 log.info("User not authorized to update vehicle with ID: {}", vehicleDTO.getId());
		return ResponseEntity.badRequest().body(new MessageResponse("INVALID USER"));
	}
}