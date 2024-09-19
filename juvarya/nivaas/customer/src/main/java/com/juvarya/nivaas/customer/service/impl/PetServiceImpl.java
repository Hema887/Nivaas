package com.juvarya.nivaas.customer.service.impl;

import java.util.Optional;

import javax.transaction.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import com.juvarya.nivaas.customer.model.PetModel;
import com.juvarya.nivaas.customer.repository.PetRepository;
import com.juvarya.nivaas.customer.service.PetService;

@Service
public class PetServiceImpl implements PetService {
	
	@Autowired
	private PetRepository petRepository;

	@Override
	@Transactional
	public PetModel addPet(PetModel petModel) {
		return petRepository.save(petModel);
	}
	@Override
	public Optional<PetModel> findById(Long id) {
		Optional<PetModel> pet = petRepository.findById(id);
        return pet.map(Optional::ofNullable).orElse(null);

    }

	@Override
	public Page<PetModel> getAll(Pageable pageable) {
		return petRepository.findAll(pageable);
	}

	@Override
	@Transactional
	public void remove(Long petId) {
		petRepository.deleteById(petId);
		
	}

}
