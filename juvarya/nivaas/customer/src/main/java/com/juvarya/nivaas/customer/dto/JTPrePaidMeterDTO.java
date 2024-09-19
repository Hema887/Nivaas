package com.juvarya.nivaas.customer.dto;

import java.util.Date;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.juvarya.nivaas.commonservice.dto.ApartmentDTO;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@JsonInclude(JsonInclude.Include.NON_NULL)
@AllArgsConstructor
@NoArgsConstructor
public class JTPrePaidMeterDTO {

	private Long id;
	private Date creationTime;
	@NotNull(message = "costPerUnit must not be null")
	private Double costPerUnit;
	private String description;
	@NotNull(message = "name must not be null")
	private String name;
//	@NotNull(message = "prepaidId must not be null")
//	private Long prepaidId;
	@NotNull(message = "apartmentId must not be null")
	private Long apartmentId;
	private ApartmentDTO apartmentDTO;

}
