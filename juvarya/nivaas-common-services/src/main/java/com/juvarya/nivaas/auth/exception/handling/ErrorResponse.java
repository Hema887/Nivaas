package com.juvarya.nivaas.auth.exception.handling;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorResponse {
	private int errorCode;
	private String errorMessage;

	public ErrorResponse(int errorCode, String errorMessage) {
		this.errorCode = errorCode;
		this.errorMessage = errorMessage;
	}

}
