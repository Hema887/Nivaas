package com.juvarya.nivaas.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ConsumptionPerFlatDTO {

    private Double unitsConsumed;
    private String flatNumber;
    private Long flatId;
}
