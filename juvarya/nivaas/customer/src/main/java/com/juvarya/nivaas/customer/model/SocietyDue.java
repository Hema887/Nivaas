package com.juvarya.nivaas.customer.model;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.juvarya.nivaas.customer.model.constants.FlatPaymentStatus;
import com.juvarya.nivaas.customer.populator.JsonDataConverter;

import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "SOCIETY_DUE")
public class SocietyDue {

	@Id
	@GeneratedValue
	private Long id;
	
	@JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd")
	@Column(name = "DUE_DATE")
	private LocalDate dueDate;

	@Column(name = "APARTMENT_ID")
	private Long apartmentId;

	@Column(name = "FLAT_ID")
	private Long flatId;

	@Column(name = "COST")
	private Double cost;

	@Enumerated(EnumType.STRING)
	@Column(name = "STATUS")
	private FlatPaymentStatus status;

	@Convert(converter = JsonDataConverter.class)
	@Column(name = "maintenance_details", columnDefinition = "TEXT")
	private String maintenanceDetails;
	
}
