package com.juvarya.nivaas.access.mgmt.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "CUSTOMER_LAST_LOGIN")
@Getter
@Setter
public class CustomerLastLoginModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "DATE")
	private Date date;

	@OneToOne
	@JoinColumn(name = "CUSTOMER")
	private User customer;

}
