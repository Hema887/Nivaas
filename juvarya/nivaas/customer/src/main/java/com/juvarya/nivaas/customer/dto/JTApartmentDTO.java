package com.juvarya.nivaas.customer.dto;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import com.juvarya.nivaas.commonservice.dto.AddressDTO;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JTApartmentDTO {

	private Long id;
	private String name;
	private String code;
	private String description;
	private Long totalBlocks;
	private String apartmentType;
	private String builderName;
	private String status;
	private boolean isUnderConstruction;
	private boolean availableForRent;
	private boolean availableForSale;
	@NotNull(message = "line1 must not be null")
	private String line1;
	private String line2;
	private String line3;
	@NotNull(message = "postalCode must not be null")
	private Long postalCode;
	@NotNull(message = "locality must not be null")
	private String locality;
	private String contactNumber;
	private Boolean defaultAddress;
	private AddressDTO addressDTO;

	
}
