package com.juvarya.nivaas.access.mgmt.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.NotNull;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserUpdateDto {

    @NotNull(message = "fullName must not be null")
    private String fullName;

    @NotNull(message = "email must not be null")
    private String email;
}
