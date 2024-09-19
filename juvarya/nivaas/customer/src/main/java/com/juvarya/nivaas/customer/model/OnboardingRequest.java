package com.juvarya.nivaas.customer.model;

import java.util.Date;
import java.util.List;

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

import com.juvarya.nivaas.customer.model.constants.OnboardType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "ONBOARDING_REQUEST", indexes = { @Index(name = "idx_onboardingid", columnList = "id", unique = true) })
@Getter
@Setter
public class OnboardingRequest {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "STATUS")
	private boolean status;

	@Column(name = "ADMIN_APPROVED")
	private boolean adminApproved;

	@Enumerated(EnumType.STRING)
	@Column(name = "TYPE")
	private OnboardType onboardType;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

	@Column(name = "MODIFICATION_TIME")
	private Date modificationTime;

	@Column(name = "APPROVED_ON")
	private Date approvedOn;

	@Column(name = "CLOSED_ON")
	private Date closedOn;

	@OneToOne
	@JoinColumn(name = "apartment_id")
	private NivaasApartmentModel apartment;

	@OneToOne
	@JoinColumn(name = "flat_id")
	private NivaasFlatModel flat;

	@Column(name = "REQUESTED_CUSTOMER")
	private Long requestedCustomer;

	@OneToMany(fetch = FetchType.EAGER)
	@JoinColumn(name = "ONBOARD_REQUEST_ID")
	private List<ApartmentAndFlatRelatedUsersModel> relatedUsers;
}
