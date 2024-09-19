package com.juvarya.nivaas.customer.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class BasicUserDetails {

	@NotNull(message = "Id must not be null")
	private Long id;

	@NotNull(message = "fullName must not be null")
	private String fullName;

	private String email;

	private boolean newUser;

	private String token;

}
