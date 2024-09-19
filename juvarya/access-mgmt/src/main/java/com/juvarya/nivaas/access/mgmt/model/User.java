package com.juvarya.nivaas.access.mgmt.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "USER", indexes = {
		@Index(name = "idx_userid", columnList = "id", unique = true) }, uniqueConstraints = {
				@UniqueConstraint(columnNames = "username"), @UniqueConstraint(columnNames = "email") })
@Getter
@Setter
public class User {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "FIRST_NAME")
	private String fullName;

	@Size(max = 20)
	private String username;

	@Size(max = 50)
	@Email
	private String email;

	@ManyToMany(fetch = FetchType.LAZY)
	@JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"), inverseJoinColumns = @JoinColumn(name = "role_id"))
	private Set<Role> roles = new HashSet<>();

	@Column(name = "PROFILE_PICTURE")
	private Long profilePicture;

	@NotBlank
	@Column(name = "PRIMARY_CONTACT")
	private String primaryContact;

	@Column(name = "GENDER")
	private String gender;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

	@Column(name = "TYPE")
	private String type;

	@Column(name = "POSTAL_CODE")
	private Long postalCode;

	@Column(name = "FCM_TOKEN")
	private String fcmToken;

	@Column(name = "VERSION")
	private int version = 1;

}
