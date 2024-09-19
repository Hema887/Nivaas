package com.juvarya.nivaas.customer.dto;

import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.juvarya.nivaas.commonservice.dto.JTUserDTO;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PetDTO {

	private Long id;

	private String breed;

	private String nickName;

	private String colour;
	@NotNull(message = "petType must not be null")
	private String petType;
	@NotNull(message = "customerId must not be null")
	private Long customerId;
	private JTUserDTO customerDto;

	
}
