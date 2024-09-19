package com.juvarya.nivaas.access.mgmt.dto.request;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class LoginRequest {
	@NotBlank
	private String primaryContact;

	@NotBlank
	private String otp;

	

}
