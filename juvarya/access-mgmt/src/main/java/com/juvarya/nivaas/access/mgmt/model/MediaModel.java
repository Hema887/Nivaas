package com.juvarya.nivaas.access.mgmt.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import java.util.Date;

@Entity
@Table(name = "MEDIA", indexes = { @Index(name = "idx_mediaid", columnList = "id", unique = true) })
@Getter
@Setter
public class MediaModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "CREATION_TIME")
	private Date creationTime;

	@Column(name = "MODIFICATION_TIME")
	private Date modificationTime;

	@Column(name = "URL")
	private String url;

	@OneToOne
	@JoinColumn(name = "FEED")
	private FeedModel feed;

	@Column(name = "FILE_NAME")
	private String fileName;

	@Column(name = "MEDIA_TYPE")
	private String mediaType;

	@Column(name = "CUSTOMER_ID")
	private Long customerId;

	@Column(name = "NAME")
	private String name;

	@Column(name = "DESCRIPTION")
	private String description;

	@Column(name = "EXTENSION")
	private String extension;

	@Column(name = "CREATED_BY")
	private Long createdBy;

	
}
