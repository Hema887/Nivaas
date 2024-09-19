package com.juvarya.nivaas.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CurrentApartmentDto {
    private CurrentUserDto user;
    private ApartmentAccessDto currentApartment;
}
