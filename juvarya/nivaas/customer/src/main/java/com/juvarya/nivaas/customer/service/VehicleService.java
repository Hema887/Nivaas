package com.juvarya.nivaas.customer.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.juvarya.nivaas.customer.model.VehicleModel;

public interface VehicleService {
	
	VehicleModel addVehicle(VehicleModel vehicle);
	
	Optional<VehicleModel> findById(Long id);
	
	void removeVehicle(Long vehicleId);
	
	Page <VehicleModel> findAllVehicles (Pageable page);
	

}
