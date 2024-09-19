package com.juvarya.nivaas.customer.controllers;

import java.util.Map;
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

import com.juvarya.nivaas.customer.dto.NoticeBoardDTO;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.NoticeBoardService;
import com.juvarya.nivaas.utils.NivaasConstants;

import lombok.extern.slf4j.Slf4j;

@SuppressWarnings("rawtypes")
@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/noticeboard")
@Slf4j
public class NoticeBoardEndPoint {

	@Autowired
	private NoticeBoardService noticeBoardService;

	@PostMapping("/save")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity saveNoticeBoard(@Valid @RequestBody NoticeBoardDTO boardDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		NoticeBoardDTO noticeBoardDTO = noticeBoardService.saveNoticeBoard(boardDTO);
		return ResponseEntity.ok().body(noticeBoardDTO);
	}

	@GetMapping("/{id}")
	public ResponseEntity getById(@PathVariable("id") Long id)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		NoticeBoardDTO boardDTO = noticeBoardService.findById(id);
		if (Objects.isNull(boardDTO)) {
			return ResponseEntity.ok().body(new MessageResponse("Notice Board Not Found"));
		}
		return ResponseEntity.ok().body(boardDTO);
	}

	@GetMapping("/list")
	public ResponseEntity getAllNotices(@RequestParam int pageNo, @RequestParam int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Map<String, Object> response = noticeBoardService.getAllNotices(pageNo, pageSize);
		if (response == null) {
			return ResponseEntity.ok().body(new MessageResponse("Notice Board is empty"));
		}
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/notices")
	@PreAuthorize(NivaasConstants.ROLE_FLAT_OWNER + " " + NivaasConstants.OR + " " + NivaasConstants.ROLE_FLAT_TENANT)
	public ResponseEntity getCustomerNotices(@RequestParam int pageNo, @RequestParam int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Map<String, Object> response = noticeBoardService.getAllUserNotices(pageNo, pageSize);
		if (response == null) {
			return ResponseEntity.ok().body(new MessageResponse("Notice Board is empty"));
		}
		return ResponseEntity.ok().body(response);
	}

	@GetMapping("/apartment/{id}")
	@PreAuthorize(NivaasConstants.ROLE_APARTMENT_ADMIN)
	public ResponseEntity getApartmentCustomerNotices(@PathVariable("id") Long apartmentId, @RequestParam int pageNo,
			@RequestParam int pageSize) throws ClassNotFoundException, InstantiationException, IllegalAccessException {
		Map<String, Object> response = noticeBoardService.getApartmentUserNotices(apartmentId, pageNo, pageSize);
		if (response == null) {
			return null;
		}
		return ResponseEntity.ok().body(response);
	}

}
