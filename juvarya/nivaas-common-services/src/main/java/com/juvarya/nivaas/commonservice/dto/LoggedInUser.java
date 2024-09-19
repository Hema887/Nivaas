package com.juvarya.nivaas.commonservice.dto;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
@Data
public class LoggedInUser {
	private Long id;

	private String email;

	private String password;

	private String fullName;

	private Set<String> roles = new HashSet<>();

	private String primaryContact;

	private boolean newUser;

	private String profilePicture;

	private int version;

	private List<OnboardingRequestDTO> flatDTO;

	

}
