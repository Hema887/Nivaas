package com.juvarya.nivaas.commonservice.dto;

import java.util.Set;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.juvarya.nivaas.commonservice.enums.ERole;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Builder
public class BasicOnboardUserDTO {
	private String username;
	private String email;
	private String fullName;
	private String primaryContact;
	private Set<ERole> userRoles;

	@JsonCreator
	public BasicOnboardUserDTO(@JsonProperty("username") String username, @JsonProperty("email") String email,
			@JsonProperty("fullName") String fullName, @JsonProperty("primaryContact") String primaryContact,
			@JsonProperty("userROles") Set<ERole> userRoles) {
		this.email = email;
		this.fullName = fullName;
		this.username = username;
		this.primaryContact = primaryContact;
		this.userRoles = userRoles;
	}

}
