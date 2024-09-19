package com.juvarya.nivaas.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ADDRESS", indexes = { @Index(name = "idx_addressid", columnList = "id", unique = true) })
@Getter
@Setter
public class AddressModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "LOCALITY")
	private String locality;

	@Column(name = "LINE1")
	private String line1;

	@Column(name = "LINE2")
	private String line2;

	@Column(name = "LINE3")
	private String line3;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

	@OneToOne
	@JoinColumn(name = "NIVAAS_CITY")
	private NivaasCityModel city;

	@Column(name = "POSTAL_CODE")
	private String postalCode;
	
	@Column(name = "CREATED_BY")
	private Long createdBy;

}
