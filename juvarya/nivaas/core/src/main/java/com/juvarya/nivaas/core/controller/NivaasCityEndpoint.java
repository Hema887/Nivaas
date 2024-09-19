package com.juvarya.nivaas.core.controller;

import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.commonservice.dto.MessageResponse;
import com.juvarya.nivaas.commonservice.dto.NivaasCityDTO;
import com.juvarya.nivaas.core.service.NivaasCityService;
import com.juvarya.nivaas.utils.NivaasConstants;

@RestController
@RequestMapping(value = "/nivaascity")
public class NivaasCityEndpoint {

	@Autowired
	private NivaasCityService nivaasCityService;

	@PostMapping("/save")
	@PreAuthorize(NivaasConstants.ROLE_USER_ADMIN)
	public ResponseEntity<?> save(@Valid @RequestBody NivaasCityDTO nivaasCityDTO) {
		nivaasCityService.save(nivaasCityDTO);
		return ResponseEntity.ok().body(new MessageResponse("NivaasCity saved"));
	}

	@GetMapping("/{id}")
	public NivaasCityDTO getbyId(@PathVariable("id") Long id)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		NivaasCityDTO cityDTO = nivaasCityService.findById(id);
		if (Objects.isNull(cityDTO)) {
			return null;
		}
		return cityDTO;
	}

	@DeleteMapping("/delete")
	@PreAuthorize(NivaasConstants.ROLE_USER_ADMIN)
	public ResponseEntity<?> deleteCity(@RequestParam Long cityId)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		NivaasCityDTO cityModel = nivaasCityService.findById(cityId);
		if (Objects.isNull(cityModel)) {
			return ResponseEntity.ok().body(new MessageResponse("Nivaas City Not Found"));
		}
		nivaasCityService.delete(cityId);
		return ResponseEntity.ok().body(new MessageResponse("Nivaas City Deleted"));
	}

	@GetMapping("/list")
	public ResponseEntity<?> getAllNivaasCities(@RequestParam int pageNo, @RequestParam int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Map<String, Object> response = nivaasCityService.findAll(pageNo, pageSize);
		if (Objects.isNull(response)) {
			return ResponseEntity.badRequest().body(new MessageResponse("Cities Empty"));
		}
		return ResponseEntity.ok().body(response);
	}

}
