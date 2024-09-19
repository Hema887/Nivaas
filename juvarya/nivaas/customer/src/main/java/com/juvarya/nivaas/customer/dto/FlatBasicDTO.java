package com.juvarya.nivaas.customer.dto;

import javax.validation.constraints.NotNull;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlatBasicDTO {
	@NotNull(message = "flatNumber must not be null")
	private String flatNo;

	@NotNull(message = "Owner phone number must not be null")
	private String ownerPhoneNo;

	@NotNull(message = "Owner name must not be null")
	private String ownerName;
}
