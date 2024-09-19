package com.juvarya.nivaas.commonservice.dto;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ApartmentDTO {

	private Long id;

	@NotNull
	private String name;

	private String code;

	private String description;

	@NotNull(message = "Total flats must not be null")
	@Min(2)
	private int totalFlats;

	private String apartmentType;

	private String builderName;

	@NotNull(message = "line1 must not be null")
	private String line1;

	private String line2;

	private String line3;

	@NotNull(message = "postalCode must not be null")
	@Size(min = 6, max = 6, message = "Postal code must be exactly 6 characters")
	private String postalCode;

	private String contactNumber;

	private Boolean defaultAddress;

	private AddressDTO addressDTO;

	private Boolean approve;

	@NotNull(message = "City must not be null")
	private Long cityId;

}
