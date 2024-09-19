package com.juvarya.nivaas.customer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class PrepaidConsumptionDto {

    @NotNull(message = "PrepaidId must not be null")
    private Long prepaidId;

    @NotNull(message = "ApartmentId must not be null")
    private Long apartmentId;

    @NotNull(message = "Flat consumption list must not be null")
    @Size(min = 1, message = "Flat consumption list must contain at least one item")
    private List<FlatConsumptionDTO> flatConsumption;
}
