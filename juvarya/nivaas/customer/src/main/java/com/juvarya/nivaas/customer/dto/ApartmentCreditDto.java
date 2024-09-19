package com.juvarya.nivaas.customer.dto;

import java.time.LocalDate;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.juvarya.nivaas.customer.annotations.MinDate;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class ApartmentCreditDto {

	@NotNull
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@MinDate(value = "2023-01-01", message = "Transaction date must be after January 1, 2023")
	private LocalDate transactionDate;
	
	@NotNull
	private String creditType;

	private String description;

	@NotNull
	private Double amount;

	@NotNull
	private Long apartmentId;
}
