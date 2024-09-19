package com.juvarya.nivaas.customer.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FlatUsageDto {
    private long prepaidMeterId;
    private Double costPerUnit;
    private Double unitsConsumed;
    private String name;
}
