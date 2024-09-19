package com.juvarya.nivaas.customer.service;

import java.util.List;
import java.util.Map;

import com.juvarya.nivaas.commonservice.dto.ApartmentDTO;
import com.juvarya.nivaas.customer.dto.request.ApartmentCoAdminDTO;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;

public interface NivaasApartmentService {

	NivaasApartmentModel saveApartment(NivaasApartmentModel nivaasApartmentModel);

	NivaasApartmentModel sendOnBoardRequestForApartment(final ApartmentDTO apartmentDTO, final Long customerId);

	NivaasApartmentModel findById(Long id);

	void removeApartment(NivaasApartmentModel nivaasApartmentModel);

	List<NivaasApartmentModel> findAll();

	List<NivaasApartmentModel> findByCreatedBy(Long createdBy);

	List<ApartmentDTO> findbyApartmentName(String name)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;

	void addCoAdmin(final ApartmentCoAdminDTO coAdminDTO);

	Map<String, Object> nearyByApartments(Long cityId, int pageNo, int pageSize)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;
}