package com.juvarya.nivaas.core.dto;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class AppDetailsDto {
	private Long id;
	
	@NonNull
	private String currentVersion;

}
