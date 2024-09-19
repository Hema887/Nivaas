package com.juvarya.nivaas.customer.dto;

import javax.validation.constraints.NotNull;

import com.fasterxml.jackson.annotation.JsonInclude;

import com.juvarya.nivaas.customer.model.constants.FlatPaymentStatus;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
public class SocietyDTO {

	private Long id;
    @NotNull(message = "apartmentId must not be null")
    private Long apartmentId;
    private Long flatId;
    private Double cost;
    private String FlatPrepaidDetails;
    private FlatPaymentStatus status;
    private String flatNo;

}
