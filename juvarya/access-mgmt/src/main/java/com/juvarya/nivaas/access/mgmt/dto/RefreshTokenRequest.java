package com.juvarya.nivaas.access.mgmt.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class RefreshTokenRequest {
	
	@NotNull
	@NotNull
	private String refreshToken;

}
