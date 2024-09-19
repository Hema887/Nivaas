package com.juvarya.nivaas.customer.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.juvarya.nivaas.customer.model.constants.NotificationType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NOTIFICATION", indexes = { @Index(name = "idx_notificationid", columnList = "id", unique = true) })
@Getter
@Setter
public class NotificationModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

	@Column(name = "MODIFICATION_TIME")
	private Date modificationTime;

	@Column(name = "MESSAGE")
	private String message;

	@Column(name = "TYPE")
	private NotificationType type;

	@OneToOne
	@JoinColumn(name = "NIVAAS_FLAT_ID")
	private NivaasFlatModel flatModel;

	@OneToOne
	@JoinColumn(name = "NIVAAS_APARTMENT_ID")
	private NivaasApartmentModel nivaasApartmentModel;

	@Column(name = "USER_ID")
	private Long userId;

	@Column(name = "TENANT_ID")
	private Long tenantId;

	@OneToOne
	@JoinColumn(name = "ONBOARD_REQUEST_ID")
	private OnboardingRequest onboardRequet;

	@OneToOne
	@JoinColumn(name = "SOCIETY_DUE_ID")
	private SocietyDue societyDue;
}
