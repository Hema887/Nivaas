package com.juvarya.nivaas.core.service;

import java.util.Map;

import com.juvarya.nivaas.commonservice.dto.NivaasCityDTO;
import com.juvarya.nivaas.core.model.NivaasCityModel;

public interface NivaasCityService {

	NivaasCityModel save(NivaasCityDTO cityDTO);

	NivaasCityDTO findById(Long id) throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	Map<String, Object> findAll(int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	void delete(Long id);

}
