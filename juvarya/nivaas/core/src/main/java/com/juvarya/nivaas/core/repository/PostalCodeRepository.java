package com.juvarya.nivaas.core.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.core.model.PostalCodeModel;

@Repository
public interface PostalCodeRepository extends JpaRepository<PostalCodeModel, Long> {
	PostalCodeModel findByCode(String code);

}
