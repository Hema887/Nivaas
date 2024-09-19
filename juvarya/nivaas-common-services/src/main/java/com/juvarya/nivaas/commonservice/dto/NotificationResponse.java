package com.juvarya.nivaas.commonservice.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class NotificationResponse {

	private int status;
	private String message;

	public NotificationResponse(int status, String message) {
		super();
		this.status = status;
		this.message = message;
	}

}
