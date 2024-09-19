package com.juvarya.nivaas.customer.dto.request;

import com.juvarya.nivaas.commonservice.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ApartmentCoAdminDTO {

    private Long apartmentId;
    private Long userId;
    private ERole userRole;
}
