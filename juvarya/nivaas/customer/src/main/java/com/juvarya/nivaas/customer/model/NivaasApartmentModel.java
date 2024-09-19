package com.juvarya.nivaas.customer.model;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.juvarya.nivaas.customer.model.constants.ApartmentType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NIVAAS_APARTMENT", indexes = { @Index(name = "idx_apartmentid", columnList = "id", unique = true) })
@Getter
@Setter
public class NivaasApartmentModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "NAME")
	private String name;

	@Column(name = "APPROVE")
	private boolean approve;

	@Column(name = "CODE")
	private String code;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "TOTAL_Flats")
	private int totalFlats;

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE")
	private ApartmentType apartmentType;

	@Column(name = "BUILDER")
	private String builderName;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	private List<NivaasFlatModel> flats = new ArrayList<>();
	
	@Column(name = "ADDRESS")
	private Long address;

}
