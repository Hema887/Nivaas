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

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "NoticeBoard", indexes = { @Index(name = "idx_noticeBoardid", columnList = "id", unique = true) })
@Getter
@Setter
public class NoticeBoardModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "TITLE")
	private String title;

	@Column(name = "BODY")
	private String body;

	@Column(name = "PUBLISH_TIME")
	private Date publishTime;

	@OneToOne
	@JoinColumn(name = "apartment_id")
	private NivaasApartmentModel apartment;

}
