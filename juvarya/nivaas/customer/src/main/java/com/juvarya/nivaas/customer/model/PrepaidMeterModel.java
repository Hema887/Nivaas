package com.juvarya.nivaas.customer.model;

import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PREPAID_METER", indexes = { @Index(name = "idx_prepaidid", columnList = "id", unique = true) })
@Getter
@Setter
public class PrepaidMeterModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

	@Column(name = "COST_PER_UNIT")
	private Double costPerUnit;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "NAME")
	private String name;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "nivaas_apartment_id")
	private NivaasApartmentModel apartmentModel;

	@ManyToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY)
	@JoinColumn(name = "maintenance_id")
	private MaintenanceModel maintenance;

}
