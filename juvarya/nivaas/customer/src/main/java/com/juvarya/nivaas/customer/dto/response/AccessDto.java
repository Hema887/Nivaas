package com.juvarya.nivaas.customer.dto.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AccessDto {
    private CurrentUserDto user;
    private List<ApartmentAccessDto> apartments;
}
