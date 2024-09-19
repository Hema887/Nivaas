package com.juvarya.nivaas.customer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.juvarya.nivaas.customer.model.constants.VehicleType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "Vehicle",indexes = { @Index(name = "idx_vehicleid", columnList = "id", unique = true) })
@Getter
@Setter
public class VehicleModel {
	
	@Id
	@GeneratedValue
	private Long id;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE" )
    private VehicleType vehicleType;
	
	@Column(name = "BRAND")
	private String brand;
	
	@Column(name = "COLOR")
	private String color;
	
	@Column(name = "VEHICLE_NUMBER")
	private Long vehicleNumber;

	private Long customerId;

	

}