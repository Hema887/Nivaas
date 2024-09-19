package com.juvarya.nivaas.customer.firebase.listeners;

import org.springframework.context.ApplicationEvent;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Notification extends ApplicationEvent {

	private static final long serialVersionUID = 1L;

	public Notification(Object source) {
		super(source);
	}

	private Long apartment;
	private Long customer;
	private boolean apartmentOnboard;
	private boolean apartmentApprove;
	private Long flat;
	private boolean flatOnboard;
	private boolean flatApprove;
	private Long societyDue;
	private String token;
	private String paid;
	private boolean due;
	private double totalCost;
	private Long tenat;
	
}
