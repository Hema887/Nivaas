package com.juvarya.nivaas.access.mgmt.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder(toBuilder = true)
@Getter
@Setter
public class AuthenticatedUser {

    private Long userId;
    private List<String> roles;
    private List<String> scopes;
    private String primaryContact;
	
}
