package com.juvarya.nivaas.commonservice.dto;

import java.util.Date;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AddressDTO {

	private Long id;

	private String locality;

	@NotBlank(message = "line1 must not be empty")
	@NotNull(message = "line1 must not be null")
	private String line1;

	private String line2;

	private String line3;

	private Date creationTime;

	@NotNull(message = "CityId must not be null")
	private Long cityId;

	private NivaasCityDTO nivaasCityDTO;

	@NotNull(message = "postalCode must not be null")
	@Size(min = 6, max = 6, message = "Postal code must be exactly 6 characters")
	private String postalCode;

	private Long createdById;

	private UserDTO createdBy;

}
