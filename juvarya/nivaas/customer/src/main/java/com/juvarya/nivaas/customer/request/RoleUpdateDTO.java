package com.juvarya.nivaas.customer.request;


import javax.validation.constraints.NotNull;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class RoleUpdateDTO {
	
	@NotNull(message = "role must not be null")
	private String role;
	@NotNull(message = "roleType must not be null")
	private String roleType;
	@NotNull(message = "primaryContact must not be null")
	private String primaryContact;
	
	
}
