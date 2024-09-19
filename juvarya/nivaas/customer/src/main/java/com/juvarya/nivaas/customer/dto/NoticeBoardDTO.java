package com.juvarya.nivaas.customer.dto;

import java.util.Date;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.juvarya.nivaas.commonservice.dto.ApartmentDTO;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class NoticeBoardDTO {

	private Long id;

	@NotNull(message = "Title cannot be null")
	@NotEmpty(message = "Title cannot be empty")
	private String title;

	@NotNull(message = "Body cannot be null")
	@Size(min = 10, max = 1000, message = "Body must be between 10 and 100 characters")
	private String body;

	private Date publishTime;

	@NotNull(message = "ApartmentId cannot be null")
	private Long apartmentId;

	private ApartmentDTO apartmentDTO;

}
