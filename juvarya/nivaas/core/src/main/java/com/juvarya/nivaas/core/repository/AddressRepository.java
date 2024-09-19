package com.juvarya.nivaas.core.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.core.model.AddressModel;
import com.juvarya.nivaas.core.model.NivaasCityModel;

@Repository
public interface AddressRepository extends JpaRepository<AddressModel, Long> {

	List<AddressModel> findByCity(NivaasCityModel cityModel);

}
