package com.juvarya.nivaas.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.core.model.NivaasCityModel;

@Repository
public interface NivaasCityRepository extends JpaRepository<NivaasCityModel, Long> {

}
