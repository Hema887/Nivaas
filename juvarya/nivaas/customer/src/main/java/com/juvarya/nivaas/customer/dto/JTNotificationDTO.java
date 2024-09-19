package com.juvarya.nivaas.customer.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.juvarya.nivaas.customer.model.constants.NotificationType;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class JTNotificationDTO {

	private Long id;
	private Date creationTime;
	private Date modificationTime;
	
	@NotNull(message = "Message must not be null")
	private String message;
	
	@NotNull(message = "Type must not be null")
	private NotificationType type;
	
	@NotNull(message = "flatId must not be null")
	private Long flatId;
	private JTFlatDTO flatDTO;

	
}
