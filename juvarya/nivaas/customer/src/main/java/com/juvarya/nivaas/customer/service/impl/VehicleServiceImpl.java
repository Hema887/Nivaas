package com.juvarya.nivaas.customer.service.impl;


import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.juvarya.nivaas.customer.model.VehicleModel;
import com.juvarya.nivaas.customer.repository.VehicleRepository;
import com.juvarya.nivaas.customer.service.VehicleService;

@Service
public class VehicleServiceImpl implements VehicleService {

	@Autowired
	private VehicleRepository vehicleRepository;
	
	@Transactional
	@Override
	public VehicleModel addVehicle(VehicleModel vehicle) {
		return vehicleRepository.save(vehicle);
	}
	
	@Transactional
	@Override
	public void removeVehicle(Long vehicleId) {
		vehicleRepository.deleteById(vehicleId);
	}
	
	@Override
	public Page<VehicleModel> findAllVehicles(Pageable page) {
		return vehicleRepository.findAll(page);
	}

	@Override
	public Optional<VehicleModel> findById(Long id) {
		return vehicleRepository.findById(id);
	}


}
