package com.juvarya.nivaas.commonservice.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class MediaDTO {
	private Long id;
	private String url;
	private String name;
	private String decsription;
	private String extension;
	private Date creationTime;

	
}
