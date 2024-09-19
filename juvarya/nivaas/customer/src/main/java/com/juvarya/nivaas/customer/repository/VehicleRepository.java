package com.juvarya.nivaas.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.customer.model.VehicleModel;

@Repository
public interface VehicleRepository extends JpaRepository<VehicleModel, Long> {
	
	 
}
