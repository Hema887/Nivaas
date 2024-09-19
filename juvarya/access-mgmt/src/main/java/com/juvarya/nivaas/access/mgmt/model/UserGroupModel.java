package com.juvarya.nivaas.access.mgmt.model;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

@Entity
@Table(name = "USER_GROUP",
indexes = {@Index(name = "idx_usergroupid",  columnList="id", unique = true)})
@Getter
@Setter
public class UserGroupModel {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "CODE")
	private String code;

	@Column(name = "PROFILE_ID")
	private Long profileId;

	
	
}
