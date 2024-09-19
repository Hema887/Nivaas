package com.juvarya.nivaas.customer.client;

import java.util.List;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

import com.juvarya.nivaas.commonservice.dto.AddressDTO;
import com.juvarya.nivaas.commonservice.dto.NivaasCityDTO;
import com.juvarya.nivaas.commonservice.dto.PostalCodeDTO;

@FeignClient(name = "nivaascore"/* , url = "${nivaas.core.url}" */)
public interface NivaasCoreClient {

	@GetMapping("/api/core/nivaascity/{id}")
	NivaasCityDTO getCityDetails(@PathVariable("id") Long id);

	@PostMapping("/api/core/address/save")
	AddressDTO saveAddress(@RequestBody AddressDTO addressDTO);

	@GetMapping("/api/core/address/{id}")
	public AddressDTO getById(@PathVariable("id") Long id);

	@GetMapping("/api/core/address/city/{cityId}")
	List<AddressDTO> getAddressByCity(@PathVariable("cityId") Long cityId);

	@PostMapping("/postalcode/save")
	PostalCodeDTO savePostalCode(@RequestBody PostalCodeDTO codeDTO);

}
