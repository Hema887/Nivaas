package com.juvarya.nivaas.commonservice.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class FlatDTO {

	private Long id;

	private String facing;

	private Integer totalRooms;

	private Double squareFeet;

	private Integer floorNo;

	@NotNull(message = "Flat number cannot be null")
	private String flatNo;

	private Boolean isAvailableForRent;

	private Boolean isAvailableForSale;

	private Boolean isParkingAvailable;

//	@NotNull(message = "Tenant ID cannot be null")
	private Long tenantId;

//	@NotNull(message = "Owner ID cannot be null")
	private Long ownerId;

	@NotNull(message = "Owner ID cannot be null")
	private Long apartmentId;

	private ApartmentDTO apartmentDTO;
	private JTUserDTO tenantDTO;
	private JTUserDTO ownerDTO;
}
