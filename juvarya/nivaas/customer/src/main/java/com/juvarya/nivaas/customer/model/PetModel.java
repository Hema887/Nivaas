package com.juvarya.nivaas.customer.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import com.juvarya.nivaas.customer.model.constants.PetType;
import lombok.Getter;
import lombok.Setter;

@Entity
@Table(name = "PET",
indexes = {@Index(name = "idx_petid",  columnList="id", unique = true)})
@Getter
@Setter
public class PetModel {
	
    @Id
	@GeneratedValue
	private Long id;
    
    @Column(name = "BREED")
    private String breed;
    
    @Column(name = "NICKNAME")
    private String nickName;
    
    @Column(name = "COLOUR")
    private String colour;
    
    @Enumerated(EnumType.STRING)
    @Column(name="TYPE")
    private PetType petType;

	private Long customerId;
}
