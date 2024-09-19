package com.juvarya.nivaas.customer.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class JTMediaDTO {
	private Long id;
	private String url;
	private String name;
	private String decsription;
	private String extension;

	

}
