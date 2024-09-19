package com.juvarya.nivaas.commonservice.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class NotificationDTO {

	private Long id;
	private Date creationTime;
	private Date modificationTime;
	private String message;
	private Long flatId;
	private FlatDTO flatDTO;
	private Long apartmentId;
	private ApartmentDTO apartmentDTO;
	private JTUserDTO jtUserDTO;
	private String type;
	private Long userId;
}
