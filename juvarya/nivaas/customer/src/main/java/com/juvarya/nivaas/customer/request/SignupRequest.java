package com.juvarya.nivaas.customer.request;

import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class SignupRequest {
	@NotBlank
	private String fullName;

	@Size(max = 50)
	@Email
	private String email;

	private Set<String> role;

	@NotBlank
	private String otp;

	@NotBlank(message = "primary contact should enter")
	private String primaryContact;

	

}
