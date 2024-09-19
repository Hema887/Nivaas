package com.juvarya.nivaas.access.mgmt.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.juvarya.nivaas.access.mgmt.model.User;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotBlank;
import javax.xml.bind.annotation.XmlElement;
import java.util.Date;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class UserOTPDTO {
	@XmlElement
	private Long id;

	@NotBlank(message = "otpType is mandatory")
	private String otpType;

	@XmlElement
	private String channel;

	private String emailAddress;

	@XmlElement
	private Long userId;

	@XmlElement
	private Date creationTime;

	@XmlElement
	private String otp;

	@XmlElement
	private User user;
	
	@NotBlank(message = "primary contact is mandatory")
	private String primaryContact;
	
	
}
