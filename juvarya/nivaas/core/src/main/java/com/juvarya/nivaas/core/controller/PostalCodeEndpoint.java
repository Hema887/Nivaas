package com.juvarya.nivaas.core.controller;

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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.commonservice.dto.MessageResponse;
import com.juvarya.nivaas.commonservice.dto.PostalCodeDTO;
import com.juvarya.nivaas.core.service.PostalCodeService;
import com.juvarya.nivaas.utils.NivaasConstants;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(value = "/postalcode")
public class PostalCodeEndpoint {
	@Autowired
	private PostalCodeService postalCodeService;

	@PostMapping("/save")
	@PreAuthorize(NivaasConstants.ROLE_USER_ADMIN)
	public PostalCodeDTO save(@Valid @RequestBody PostalCodeDTO postalCodeDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return postalCodeService.save(postalCodeDTO);
	}

	@GetMapping("/{id}")
	public ResponseEntity<?> getById(@PathVariable("id") Long id)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		PostalCodeDTO postalCodeDTO = postalCodeService.findById(id);
		if (Objects.isNull(postalCodeDTO)) {
			return ResponseEntity.badRequest().body("PostalCode Not Found");
		}
		return ResponseEntity.ok().body(postalCodeDTO);
	}

	@DeleteMapping("/delete")
	@PreAuthorize(NivaasConstants.ROLE_USER_ADMIN)
	public ResponseEntity<?> deletePostalCode(@RequestParam Long postalCodeId)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		PostalCodeDTO codeDTO = postalCodeService.findById(postalCodeId);
		if (Objects.isNull(codeDTO)) {
			return ResponseEntity.badRequest().body("PostalCode Not Found");
		}
		postalCodeService.delete(postalCodeId);
		return ResponseEntity.ok().body(new MessageResponse("PostalCode Deleted"));
	}

	@GetMapping("/list")
	public ResponseEntity<?> getAllPostalCodes(@RequestParam int pageNo, @RequestParam int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Map<String, Object> response = postalCodeService.findAll(pageNo, pageSize);
        return ResponseEntity.ok().body(Objects.requireNonNullElseGet(response, () -> new MessageResponse("PostalCode Empty")));
    }

	@GetMapping("/code")
	public PostalCodeDTO getByPostalcode(@RequestParam String postalCode)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		return postalCodeService.findByCode(postalCode);
	}

}
