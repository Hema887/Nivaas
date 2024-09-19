package com.juvarya.nivaas.core.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NIVAAS_CITY")
@Getter
@Setter
public class NivaasCityModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "ISOCODE")
	private String isoCode;

	@Column(name = "NAME")
	private String name;

	@Column(name = "COUNTRY")
	private String country;

	@Column(name = "REGION")
	private String region;

	@Column(name = "DISTRICT")
	private String district;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "nivaas_city_id", referencedColumnName = "id")
	private List<PostalCodeModel> PostalCodes;

}
