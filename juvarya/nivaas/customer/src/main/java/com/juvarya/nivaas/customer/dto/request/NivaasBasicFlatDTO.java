package com.juvarya.nivaas.customer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class NivaasBasicFlatDTO {

	@NotNull(message = "Flat Number cannot be null")
	private String flatNo;

}
