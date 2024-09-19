package com.juvarya.nivaas.commonservice.dto;

import java.util.Date;
import java.util.Set;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UserDTO {

	private Long id;

	private String fullName;

	@Size(max = 20)
	private String username;

	@Size(max = 50)
	@Email
	private String email;

	private Set<Role> roles;

	private Long profilePicture;

	@NotBlank
	private String primaryContact;

	private String gender;

	private Date creationTime;

	private String type;

	private String token;

	private int version;

	private String fcmToken;
}
