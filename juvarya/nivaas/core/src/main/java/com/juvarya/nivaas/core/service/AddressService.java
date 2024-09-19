package com.juvarya.nivaas.core.service;

import java.util.List;
import java.util.Map;

import com.juvarya.nivaas.commonservice.dto.AddressDTO;

public interface AddressService {

	AddressDTO save(AddressDTO addressDTO)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	void delete(Long id);

	Map<String, Object> findAll(int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	AddressDTO findById(Long id) throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	List<AddressDTO> findByCity(Long cityId)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;
}
