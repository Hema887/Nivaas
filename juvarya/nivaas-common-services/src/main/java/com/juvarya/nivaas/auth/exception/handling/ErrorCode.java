package com.juvarya.nivaas.auth.exception.handling;

import org.springframework.http.HttpStatus;

public enum ErrorCode {
	// User
	USER_NOT_FOUND(1000, "User Not Found", HttpStatus.NOT_FOUND),
	EMAIL_ALREADY_IN_USE(1001, "Email Is Already In Use", HttpStatus.CONFLICT),

	// City
	CITY_NOT_FOUND(1002, "City Not Found", HttpStatus.NOT_FOUND),

	// Apartment
	APARTMENT_NOT_FOUND(1003, "Apartment Not Found", HttpStatus.NOT_FOUND),
	APARTMENT_NOT_ALLOWED(1004, "You Are Not Allowed To Remove Apartment", HttpStatus.FORBIDDEN),
	APARTMENT_UPDATE_NOT_ALLOWED(1005, "You Are Not Allowed To Update Apartment Details", HttpStatus.FORBIDDEN),

	// Flat
	FLAT_NOT_FOUND(1006, "Flat Not Found", HttpStatus.NOT_FOUND),
	FLAT_ALREADY_EXISTS(1007, "Flat Number Already Exists In The Apartment", HttpStatus.CONFLICT),
	FLAT_CREATE_NOT_ALLOWED(1008, "You Are Not Allowed To Create Flat Details", HttpStatus.FORBIDDEN),
	FLAT_APPROVAL_NOT_ALLOWED(1009, "You Are Not Allowed To Approve Flat", HttpStatus.FORBIDDEN),
	FLAT_NOT_APPROVED(1010, "Flat Is Not Approved", HttpStatus.FORBIDDEN),
	FLAT_ALREADY_APPROVED(1011, "Flat Already Approved", HttpStatus.CONFLICT),
	FLAT_UPDATE_NOT_ALLOWED(1012, "You Are Not Allowed To Update Flat Details", HttpStatus.FORBIDDEN),

	// OnboardRequest
	REQUEST_NOT_FOUND(1013, "Onboard Request Not Found", HttpStatus.NOT_FOUND),
	FLAT_ONBOARD_NOT_ALLOWED(1014, "You Are Not Allowed To Onboard Flats", HttpStatus.FORBIDDEN),

	// Maintenance
	MAINTENANCE_MENTION(1015, "Maintenance Should Be Mention", HttpStatus.BAD_REQUEST),
	MAINTENANCE_CREATE_NOT_ALLOWED(1016, "You Are Not Allowed To Create Maintenance", HttpStatus.FORBIDDEN),

	// PrePaidMeter
	PREPAID_METER_REQUIRED(1017, "Prepaid Meter Id Is Required", HttpStatus.BAD_REQUEST),
	PREPAID_METER_CREATION_NOT_ALLOWED(1018, "You Are Not Allowed Create Prepaid Meter", HttpStatus.FORBIDDEN),
	PREPAID_METER_UPDATE_NOT_ALLOWED(1019, "You Are Not Allowed Update Prepaid Meter", HttpStatus.FORBIDDEN),
	PREPAID_METER_DELETE_NOT_ALLOWED(1020, "You Are Not Allowed Delete Prepaid Meter", HttpStatus.FORBIDDEN),
	PREPAID_METER_NOT_FOUND(1021, "PrePaid Meter Not Found", HttpStatus.NOT_FOUND),
	NO_PREPAID_METERS_FOUND(1021, "No prepaid Meters Found", HttpStatus.NOT_FOUND),
	NAME_ALREADY_EXISTS(1023, "Prepaid Meter Name Already Exists", HttpStatus.CONFLICT),

	// PrePaidMeter FlatUsage
	CONSUMPTION_NOT_ALLOWED(1024, "You Are Not Allowed To Update Consumption Units", HttpStatus.FORBIDDEN),

	INSUFFICIENT_DETAILS(1025, "Insufficient Details", HttpStatus.BAD_REQUEST),
	FLAT_LIMIT(1026, "Cannot onboard more than %d flats", HttpStatus.BAD_REQUEST),
	NOT_SUPPORTED(1027, "Not supported", HttpStatus.NOT_ACCEPTABLE),
	NOT_VALID(1028, "Not valid request", HttpStatus.BAD_REQUEST),
	DUPLICATE(1029, "Already exists", HttpStatus.CONFLICT),
	FLAT_NOT_AVAILABLE_FOR_RENT(1030, "Flat not available for rent", HttpStatus.FORBIDDEN),
	NOT_FOUND(1031, "Not found", HttpStatus.NOT_FOUND),
	OWNER_INVALID_TENANT_REQUEST(1032, "You are already owner for this flat", HttpStatus.BAD_REQUEST),
	INVALID_REFRESH_TOKEN(1033, "Invalid refresh token", HttpStatus.BAD_REQUEST),
	TOKEN_PROCESSING_ERROR(1035, "Error processing refresh token", HttpStatus.INTERNAL_SERVER_ERROR),
	APPINFO_NOT_FOUND(1036, "Appinfo not found with given id", HttpStatus.INTERNAL_SERVER_ERROR),
	FLAT_OWNER_AVAILABLE(1036, "Flat owner alredy exists", HttpStatus.INTERNAL_SERVER_ERROR);

	private final int code;
	private final String message;
	private final HttpStatus httpStatus;

	ErrorCode(int code, String message, HttpStatus httpStatus) {
		this.code = code;
		this.message = message;
		this.httpStatus = httpStatus;
	}

	public int getCode() {
		return code;
	}

	public String getMessage() {
		return message;
	}

	public HttpStatus getHttpStatus() {
		return httpStatus;
	}

	public String formatMessage(Object... args) {
		return String.format(message, args);
	}
}
