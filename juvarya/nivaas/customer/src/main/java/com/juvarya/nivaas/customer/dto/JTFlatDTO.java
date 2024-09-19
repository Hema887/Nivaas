package com.juvarya.nivaas.customer.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.juvarya.nivaas.commonservice.dto.ApartmentDTO;
import com.juvarya.nivaas.commonservice.dto.JTUserDTO;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class JTFlatDTO {

	private Long id;

	private String facing;

	private Integer totalRooms;

	private Double squareFeet;

	private String floorNo;

	private Boolean isAvailableForRent;

	private Boolean isAvailableForSale;

	private Boolean isParkingAvailable;

	@NotNull(message = "Tenant ID cannot be null")
	private Long tenantId;

	@NotNull(message = "Flat number cannot be null")
	private Long flatNo;

	@NotNull(message = "Owner ID cannot be null")
	private Long ownerId;

	@NotNull(message = "Owner ID cannot be null")
	private Long apartmentId;

	private ApartmentDTO apartmentDTO;
	private JTUserDTO tenantDTO;
	private JTUserDTO ownerDTO;
}
