package com.juvarya.nivaas.customer.service;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.juvarya.nivaas.customer.model.PetModel;

public interface PetService {
	
	PetModel addPet(PetModel petModel);
	
	Optional<PetModel> findById(Long id);

	Page<PetModel> getAll(Pageable pageable);

	void remove(Long petId);



}
