package com.juvarya.nivaas.customer.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PREPAID_FLAT_USAGE", uniqueConstraints = {
		@UniqueConstraint(columnNames = { "FLAT_ID", "PREPAID_METER_ID" }) })
@Getter
@Setter
public class PrepaidFlatUsageModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

	@Column(name = "FLAT_ID")
	private Long flatId;

	@Column(name = "APARTMENT_ID")
	private Long apartmentId;

	@Column(name = "UNITS_CONSUMED")
	private Double unitsConsumed;

	@Column(name = "PREPAID_METER_ID")
	private Long prepaidMeterId;

}
