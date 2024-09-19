package com.juvarya.nivaas.customer.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UpdateApartmentDTO {

	@NotNull(message = "postalCode must not be null")
	private Long id;

	private String code;

	private String name;

	private String description;

	private String apartmentType;

	private int totalFlats;

	private String builderName;

}
