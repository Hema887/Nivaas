package com.juvarya.nivaas.customer.populator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.juvarya.nivaas.commonservice.dto.ApartmentDTO;
import com.juvarya.nivaas.commonservice.dto.FlatDTO;
import com.juvarya.nivaas.commonservice.dto.OnboardingRequestDTO;
import com.juvarya.nivaas.customer.model.OnboardingRequest;
import com.juvarya.nivaas.customer.model.constants.OnboardType;
import com.juvarya.nivaas.utils.converter.Populator;

@Component
public class OnboardingRequestPopulator implements Populator<OnboardingRequest, OnboardingRequestDTO> {
	private static final String APARTMENT = "APARTMENT";
	private static final String FLAT = "FLAT";

	@Autowired
	private FlatPopulator flatPopulator;

	@Autowired
	private ApartmentPopulator apartmentPopulator;

	public void populate(OnboardingRequest source, OnboardingRequestDTO target) {
		target.setId(source.getId());
		target.setAdminApproved(source.isAdminApproved());
		target.setCreationTime(source.getCreationTime());
		target.setModificationTime(source.getModificationTime());
		target.setStatus(source.isStatus());

		if (null != source.getOnboardType()) {
			if (source.getOnboardType().equals(OnboardType.APARTMENT)) {
				target.setType(APARTMENT);
			} else if (source.getOnboardType().equals(OnboardType.FLAT)) {
				target.setType(FLAT);
			}
		}

		if (null != source.getFlat()) {
			FlatDTO dto = new FlatDTO();
			flatPopulator.populate(source.getFlat(), dto);
			target.setFlatId(source.getFlat().getId());
			target.setFlatDTO(dto);
		}

		if (null != source.getApartment()) {
			ApartmentDTO apartmentDTO = new ApartmentDTO();
			apartmentPopulator.populate(source.getApartment(), apartmentDTO);
			target.setApartment(source.getApartment().getId());
			target.setApartmentDTO(apartmentDTO);
		}

	}
}
