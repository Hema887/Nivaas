package com.juvarya.nivaas.customer.client;

import javax.validation.Valid;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;

import com.juvarya.nivaas.commonservice.dto.BasicOnboardUserDTO;
import com.juvarya.nivaas.commonservice.dto.LoggedInUser;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.commonservice.dto.Role;
import com.juvarya.nivaas.commonservice.dto.UserDTO;

import java.util.List;

@FeignClient(name = "access-mgmt"/*, url = "${nivaas.access-mgmt.url}"*/)
public interface AccessMgmtClient {

	@GetMapping("/api/access-mgmt/role/find/{erole}")
	Role getByERole(@PathVariable("erole") ERole eRole);

	@GetMapping("/api/access-mgmt/nivaas/auth/currentCustomer")
	ResponseEntity<LoggedInUser> getCurrentCustomer();

	@PostMapping("/api/access-mgmt/user/save")
	UserDTO saveUser(@RequestBody UserDTO user);

	@GetMapping("/api/access-mgmt/user/find/{userId}")
    UserDTO getUserById(@PathVariable("userId") Long userId);

	@PostMapping("/api/access-mgmt/user/details/all")
	List<UserDTO> getUserDetailsByIds(@RequestBody List<Long> userIds);

	@PutMapping("/api/access-mgmt/user/user/{userId}/role/{role}")
	void addRole(@PathVariable("userId") Long userId, @PathVariable("role") ERole eRole);

	@GetMapping("/api/access-mgmt/user/contact")
	LoggedInUser getByPrimaryContact(@Valid @RequestParam("primaryContact") String primaryContact);

	@PostMapping("/api/access-mgmt/user/onboard/user")
	Long onBoardUser(@Valid @RequestBody BasicOnboardUserDTO basicOnboardUserDTO);

	@DeleteMapping("/api/access-mgmt/user/contact/{mobileNumber}/role/{role}")
	void removeUserRole(@PathVariable("mobileNumber") String mobileNumber, @PathVariable("role") ERole userRole);
}
