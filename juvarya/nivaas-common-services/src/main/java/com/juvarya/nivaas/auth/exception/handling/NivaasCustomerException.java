package com.juvarya.nivaas.auth.exception.handling;

import lombok.Getter;

@Getter
public class NivaasCustomerException extends RuntimeException {
	private final ErrorCode errorCode;

	public NivaasCustomerException(ErrorCode errorCode) {
		super(errorCode.getMessage());
		this.errorCode = errorCode;
	}

	public NivaasCustomerException(ErrorCode errorCode, String message) {
		super(message);
		this.errorCode = errorCode;
	}

}
