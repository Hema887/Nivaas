package com.juvarya.nivaas.customer.service;

import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.juvarya.nivaas.customer.dto.request.FlatNumbersOnboardDto;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import com.juvarya.nivaas.customer.dto.FlatBasicDTO;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;

public interface NivaasFlatService {

	NivaasFlatModel save(NivaasFlatModel nivaasFlatModel);

	void saveAll(List<NivaasFlatModel> nivaasFlatModel);

	@SuppressWarnings("rawtypes")
	ResponseEntity updateBasicFlatDetails(final Long apartmentId, final Long flatId, final FlatBasicDTO flatBasicDTO);

	void removeFlat(NivaasFlatModel nivaasFlatModel);

	NivaasFlatModel findById(Long id);

	Page<NivaasFlatModel> getAll(Pageable pageable);

	Page<NivaasFlatModel> getFlatsByApartment(Long apartmentId, Pageable page);

	List<NivaasFlatModel> getAllFlatsByApartment(Long apartmentId);

	Map<Long, String> getFlatsMapByApartment(Long apartmentId);

	List<NivaasFlatModel> findByOwner(Long id);

	Optional<NivaasFlatModel> findByJtApartmentAndFlatId(Long jtApartmentId, Long flatId);

	boolean checkFlatExists(Long apartmentId, String flatNo);

	void bulkAddWithoutOwners(final FlatNumbersOnboardDto withoutOwnerDto);

}
