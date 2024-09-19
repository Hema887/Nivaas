package com.juvarya.nivaas.customer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.customer.model.NivaasApartmentModel;

@Repository
public interface NivaasApartmentRepository extends JpaRepository<NivaasApartmentModel, Long> {

	List<NivaasApartmentModel> findByCreatedBy(Long createdBy);

	List<NivaasApartmentModel> findByNameContainingIgnoreCase(String name);

}
