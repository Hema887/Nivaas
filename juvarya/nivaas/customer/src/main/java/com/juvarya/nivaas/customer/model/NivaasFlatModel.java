package com.juvarya.nivaas.customer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Getter
@Setter
@Table(name = "NIVAAS_FLAT")
public class NivaasFlatModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "FLAT_NO")
	private String flatNo;

	@Column(name = "FACING")
	private String facing;

	@Column(name = "TOTAL_ROOMS")
	private Integer totalRooms;

	@Column(name = "SQUARE_FEET")
	private Double squareFeet;

	@Column(name = "AVAILABLE_FOR_RENT")
	private boolean isAvailableForRent;

	@Column(name = "AVAILABLE_FOR_SALE")
	private boolean isAvailableForSale;

	@Column(name = "PARKING_AVAILABLE")
	private boolean isParkingAvailable;

	@Column(name = "FLOOR_NO")
	private Integer floorNo;

	@Column(name = "OWNER_ID")
	private Long ownerId;

	@Column(name = "TENANT_Id")
	private Long tenantId;

	@OneToOne
	@JoinColumn(name = "nivaas_apartment_id")
	private NivaasApartmentModel apartment;

}
