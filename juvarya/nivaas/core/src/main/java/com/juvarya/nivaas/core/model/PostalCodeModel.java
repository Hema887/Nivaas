package com.juvarya.nivaas.core.model;

import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "POSTALCODE", indexes = { @Index(name = "idx_postalid", columnList = "id", unique = true) })
@Getter
@Setter
public class PostalCodeModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "CODE")
	private String code;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

	@Column(name = "NIVAAS_CITY_ID")
	private Long nivaasCityId;

}
