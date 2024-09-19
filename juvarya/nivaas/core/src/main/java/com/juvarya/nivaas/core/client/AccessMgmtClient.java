package com.juvarya.nivaas.core.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import com.juvarya.nivaas.commonservice.dto.UserDTO;

@FeignClient(name = "access-mgmt")
public interface AccessMgmtClient {

	@GetMapping("/api/access-mgmt/user/find/{userId}")
	UserDTO getUserById(@PathVariable("userId") Long userId);

}
