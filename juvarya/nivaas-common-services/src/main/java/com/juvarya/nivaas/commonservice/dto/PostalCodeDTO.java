package com.juvarya.nivaas.commonservice.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PostalCodeDTO {

	private Long id;

	@NotNull(message = "code must not be null")
	@Size(min = 6, max = 6, message = "Postal code must be exactly 6 characters")
	private String code;

	private Date creationTime;

}