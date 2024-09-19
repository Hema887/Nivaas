package com.juvarya.nivaas.access.mgmt.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

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

	private String fcmToken;
}
