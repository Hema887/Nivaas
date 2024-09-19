package com.juvarya.nivaas.customer.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "APARTMENT_USER_ROLE")
@Getter
@Setter
public class ApartmentUserRoleModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "ROLE_NAME")
	private String roleName;

	@ManyToOne
	@JoinColumn(name = "nivaas_apartment_id")
	private NivaasApartmentModel apartmentModel;

	private Long createdBy;

	private Long customerId;

	@Column(name = "CREATION_TIME")
	private Date creationTime;
	
	@Column(name = "APPROVE")
	private Boolean approve;

}
