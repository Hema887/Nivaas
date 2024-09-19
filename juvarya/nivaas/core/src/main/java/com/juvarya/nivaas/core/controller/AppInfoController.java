package com.juvarya.nivaas.core.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.juvarya.nivaas.commonservice.dto.MessageResponse;
import com.juvarya.nivaas.core.dto.AppDetailsDto;
import com.juvarya.nivaas.core.model.AppInfo;
import com.juvarya.nivaas.core.service.AppInfoService;

@RestController
@RequestMapping(value = "/appinfo")
public class AppInfoController {

	@Autowired
	private AppInfoService appInfoService;

	@SuppressWarnings("rawtypes")
	@PostMapping("/save")
	public ResponseEntity saveAppInfo(@RequestBody @Valid AppDetailsDto appDetailsDto) {
		appInfoService.save(appDetailsDto);
		return ResponseEntity.ok().body(new MessageResponse("Created Successfully"));
	}

	@SuppressWarnings("rawtypes")
	@GetMapping("/{id}")
	public ResponseEntity getById(@PathVariable("id") Long id) {
		AppInfo appInfo = appInfoService.findById(id);
		return ResponseEntity.ok(appInfo);
	}

	@SuppressWarnings("rawtypes")
	@GetMapping("/list")
	public ResponseEntity getAll() {
		List<AppInfo> appInfos = appInfoService.findAll();
		return ResponseEntity.ok(appInfos);
	}

	@SuppressWarnings("rawtypes")
	@PutMapping("/update")
	public ResponseEntity updateAppInfo(@RequestBody @Valid AppDetailsDto appDetailsDto) {
		appInfoService.updateAppInfo(appDetailsDto);
		return ResponseEntity.ok().body(new MessageResponse("Updated Successfully"));
	}

	@SuppressWarnings("rawtypes")
	@DeleteMapping("/delete")
	public ResponseEntity deleteAppInfo(@RequestParam Long appInfoId) {
		appInfoService.deleteAppInfo(appInfoId);
		return ResponseEntity.ok().body(new MessageResponse("Deleted Successfully"));
	}

}
