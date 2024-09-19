package com.juvarya.nivaas.commonservice.dto;

import java.util.Date;
import java.util.List;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class NivaasCityDTO {

	private Long id;

	@NotBlank(message = "name must not be empty")
	@NotNull(message = "name must not be null")
	private String name;

	private String isoCode;

	@NotBlank(message = "Country must not be empty")
	@NotNull(message = "Country must not be null")
	private String country;

	@NotBlank(message = "Region must not be empty")
	@NotNull(message = "Region must not be null")
	private String region;

	private String district;

	private Date creationTime;

	@NotNull(message = "Codes must not be null")
	private List<String> codes;

}
