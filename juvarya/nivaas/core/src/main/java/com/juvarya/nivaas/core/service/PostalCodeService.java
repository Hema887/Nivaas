package com.juvarya.nivaas.core.service;

import java.util.Map;

import com.juvarya.nivaas.commonservice.dto.PostalCodeDTO;
import com.juvarya.nivaas.core.model.PostalCodeModel;

public interface PostalCodeService {
	PostalCodeDTO save(PostalCodeDTO codeDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	Map<String, Object> findAll(int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	PostalCodeModel delete(Long id);

	PostalCodeDTO findById(Long id) throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	PostalCodeDTO findByCode(String code) throws ClassNotFoundException, InstantiationException, IllegalAccessException;
}
