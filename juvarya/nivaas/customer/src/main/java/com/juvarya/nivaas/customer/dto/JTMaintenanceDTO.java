package com.juvarya.nivaas.customer.dto;

import java.util.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JTMaintenanceDTO {

	private Long id;
	private Date creationTime;

	@NotNull(message = "Value cannot be null")
	@Min(value = 1, message = "Value must be at least 1")
	@Max(value = 31, message = "Value must be at most 31")
	private int notifyOn;
	
	@NotNull(message = "Cost must not be null")
	private Double cost;

	@NotNull(message = "ApartmentId must not be null")
	private Long apartmentId;
	private List<Long> prepaidId;
	
	private List<JTPrePaidMeterDTO> jtPrePaidMeterDTOs;
}
