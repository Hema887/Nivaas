package com.juvarya.nivaas.core.controller;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.commonservice.dto.AddressDTO;
import com.juvarya.nivaas.commonservice.dto.MessageResponse;
import com.juvarya.nivaas.core.service.AddressService;

@RestController
@RequestMapping(value = "/address")
public class AddressEndpoint {

	@Autowired
	private AddressService addressService;

	@PostMapping("/save")
	public AddressDTO saveAddress(@Valid @RequestBody AddressDTO addressDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return addressService.save(addressDTO);
	}

	@GetMapping("/{id}")
	public AddressDTO getById(@PathVariable("id") Long id)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		AddressDTO addressDTO = addressService.findById(id);
		if (Objects.isNull(addressDTO)) {
			return null;
		}
		return addressDTO;
	}

	@DeleteMapping("/delete")
	public ResponseEntity<?> deleteAddress(@RequestParam Long addressId)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		AddressDTO addressDTO = addressService.findById(addressId);
		if (Objects.isNull(addressDTO)) {
			return ResponseEntity.ok().body(new MessageResponse("Address Not Found"));
		}
		addressService.delete(addressId);
		return ResponseEntity.ok().body(new MessageResponse("Address Deleted"));
	}

	@GetMapping("/list")
	public ResponseEntity<?> getAllAddress(@RequestParam int pageNo, @RequestParam int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Map<String, Object> response = addressService.findAll(pageNo, pageSize);
		if (null == response) {
			return ResponseEntity.badRequest().body(new MessageResponse("Address empty"));
		}
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/city/{cityId}")
	public List<AddressDTO> getAddressByCity(@PathVariable("cityId") Long cityId)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return addressService.findByCity(cityId);
	}
}
