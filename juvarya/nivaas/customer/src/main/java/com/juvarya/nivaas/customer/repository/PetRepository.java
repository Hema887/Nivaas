package com.juvarya.nivaas.customer.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.customer.model.PetModel;

@Repository
public interface PetRepository extends JpaRepository<PetModel, Long> {


}
