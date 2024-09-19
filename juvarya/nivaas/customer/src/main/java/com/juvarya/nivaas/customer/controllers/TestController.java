package com.juvarya.nivaas.customer.controllers;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@CrossOrigin(origins = "*", maxAge = 3600)
@RestController
@RequestMapping("/api/test")
public class TestController {
	@GetMapping("/all")
	public String allAccess() {
		return "Public Content.";
	}

	@GetMapping("/user")
	@PreAuthorize("hasRole('USER')or hasRole('USER_ADMIN')")
	public String userAccess() {
		return "LoggedInUser Content.";
	}

	@GetMapping("/admin")
	@PreAuthorize("hasRole('USER_ADMIN')")
	public String userAdminAccess() {
		return "Nivaas Admin.";
	}

	@GetMapping("/apartment_admin")
	@PreAuthorize("hasRole('APARTMENT_ADMIN')")
	public String apartmentAdminAccess() {
		return "Apartment Approve.";
	}

	@GetMapping("/flat_owner")
	@PreAuthorize("hasRole('FLAT_OWNER')")
	public String flatOwnerAccess() {
		return "FlatOwner Approve.";
	}

	@GetMapping("/flat_tenant")
	@PreAuthorize("hasRole('FLAT_TENANT')")
	public String flatTenantAccess() {
		return "FlatTenant Approve.";
	}

	@GetMapping("/apartment_helper")
	@PreAuthorize("hasRole('APARTMENT_HELPER')")
	public String apartmentHelperAccess() {
		return "Apartment Helper Approve.";
	}
}
