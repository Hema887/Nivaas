package com.juvarya.nivaas.customer.dto;

import java.util.Date;

import com.fasterxml.jackson.annotation.JsonInclude;

import lombok.Getter;
import lombok.Setter;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Getter
@Setter
public class OnboardingRequestDTO {

	private Long id;

	private boolean status;

	private boolean ownerApproved;

	private boolean adminApproved;

	private boolean tenantMaintanance;

	private boolean ownerMaintanance;

	private Date creationTime;

	private Date modificationTime;

	private Date approvedOn;

	private Date closedOn;

	private Long apartment;

	private Long flatId;

	private Long requestCustomerId;

	private JTFlatDTO jtFlatDTO;

	private Long tenantId;

	private String primaryContact;

	private String type;

}
