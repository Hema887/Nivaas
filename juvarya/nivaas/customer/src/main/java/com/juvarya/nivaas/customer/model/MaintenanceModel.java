package com.juvarya.nivaas.customer.model;

import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "MAINTENANCE", indexes = { @Index(name = "idx_maintenanceid", columnList = "id", unique = true) })
@Getter
@Setter
public class MaintenanceModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

	@Column(name = "NOTIFY_ON")
	private int notifyOn;

	@Column(name = "COST")
	private Double cost;

	@OneToOne
	@JoinColumn(name = "nivaas_apartment_id")
	private NivaasApartmentModel apartmentModel;

	@OneToMany(mappedBy = "maintenance", cascade = CascadeType.ALL)
	private List<PrepaidMeterModel> meters;

}
