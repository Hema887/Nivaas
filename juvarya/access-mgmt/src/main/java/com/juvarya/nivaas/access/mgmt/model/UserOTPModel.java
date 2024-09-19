package com.juvarya.nivaas.access.mgmt.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "USER_OTP",
indexes = {@Index(name = "idx_userotpid",  columnList="id", unique = true)})
@Getter
@Setter
public class UserOTPModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "OTP_TYPE")
	private String otpType;

	@Column(name = "CHANNEL")
	private String channel;

	@Column(name = "EMAIL_ADDRESS")
	private String emailAddress;

	@ManyToOne(cascade = { CascadeType.PERSIST, CascadeType.MERGE }, optional = true)
	private User user;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

	@Column(name = "OTP")
	private String otp;

	@Column(name = "PRIMARY_CONTACT")
	private String primaryContact;

	
}
