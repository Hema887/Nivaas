package com.juvarya.nivaas.commonservice.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class JTUserDTO {
	private Long id;
	private String username;
	private String email;
	private String url;
	private String fullName;
	private String primaryContact;
	private String type;

}
