package com.juvarya.nivaas.customer.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.juvarya.nivaas.customer.model.constants.DebitType;
import com.juvarya.nivaas.customer.annotations.MinDate;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class ApartmentDebitDto {

    @NotNull
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
    @MinDate(value = "2023-01-01", message = "Transaction date must be after January 1, 2023")
    private LocalDate transactionDate;

    private DebitType type;

    private String description;

    @NotNull
    private Double amount;

    @NotNull
    private Long apartmentId;
}
