package com.juvarya.nivaas.customer.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.NoticeBoardModel;

@Repository
public interface NoticeBoardRepository extends JpaRepository<NoticeBoardModel, Long> {

	Page<NoticeBoardModel> findByApartment(NivaasApartmentModel apartmentModel, Pageable pageable);

}
