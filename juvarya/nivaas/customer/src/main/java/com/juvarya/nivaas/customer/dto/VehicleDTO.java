package com.juvarya.nivaas.customer.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.juvarya.nivaas.commonservice.dto.JTUserDTO;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class VehicleDTO {
	
	private Long id;
	
	@NotNull(message = "VehicleType must not be null")
	private String vehicleType;
	
	private String brand;
	
	private String color;
	
	@NotNull(message = "VehicleNumber must not be null")
	private Long vehicleNumber;
	 
	@NotNull(message = "CustomerId must not be null")
	private Long customerId;
	private JTUserDTO customerDto;

}
