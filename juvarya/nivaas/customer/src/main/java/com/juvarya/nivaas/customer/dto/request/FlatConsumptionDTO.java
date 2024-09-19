package com.juvarya.nivaas.customer.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FlatConsumptionDTO {

    private Double unitsConsumed;
    private Long flatId;

}
