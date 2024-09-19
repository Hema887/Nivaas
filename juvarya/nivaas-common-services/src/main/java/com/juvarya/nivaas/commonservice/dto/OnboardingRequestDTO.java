package com.juvarya.nivaas.commonservice.dto;

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

	private boolean adminApproved;

	private Date creationTime;

	private Date modificationTime;

	private Date approvedOn;

	private Date closedOn;

	private Long apartment;

	private Long flatId;

	private Long requestCustomerId;

	private JTUserDTO jtUserDTO;

	private FlatDTO flatDTO;

	private Long tenantId;

	private String primaryContact;

	private String type;

	private String relatedType;
	
	private ApartmentDTO apartmentDTO;

	private Long relatedRequestId;

}
