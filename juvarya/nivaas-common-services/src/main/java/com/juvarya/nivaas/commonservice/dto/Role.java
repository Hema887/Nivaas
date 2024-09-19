package com.juvarya.nivaas.commonservice.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.juvarya.nivaas.commonservice.enums.ERole;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Role {
    private Long id;

    private ERole name;

    @JsonCreator
    public Role(@JsonProperty("name") String name) {
        this.name = ERole.valueOf(name);
    }

}