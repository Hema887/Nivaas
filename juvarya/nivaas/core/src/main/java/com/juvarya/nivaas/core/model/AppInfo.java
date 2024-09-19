package com.juvarya.nivaas.core.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "APPINFO")
@Getter
@Setter
public class AppInfo {

	@Id
	@GeneratedValue
	private Long id;

	@Column(name = "CURRENT_VERSION")
	private String currentVersion;
}
