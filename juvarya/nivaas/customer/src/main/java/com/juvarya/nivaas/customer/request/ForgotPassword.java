package com.juvarya.nivaas.customer.request;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ForgotPassword {

	private String otp;
	private String email;
	private String password;
	private String primaryContact;

	
}
