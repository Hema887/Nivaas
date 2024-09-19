package com.juvarya.nivaas.auth.exception.handling;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class NivaasGlobalExceptionHandler {

	@ExceptionHandler(NivaasCustomerException.class)
	public ResponseEntity<ErrorResponse> handleNivaasCustomException(NivaasCustomerException customException) {
		ErrorCode errorCode = customException.getErrorCode();
		ErrorResponse errorResponse = new ErrorResponse(errorCode.getCode(), customException.getMessage());
		return new ResponseEntity<>(errorResponse, errorCode.getHttpStatus());
	}

}
