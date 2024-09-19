package com.juvarya.nivaas.customer.populator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.juvarya.nivaas.commonservice.dto.ApartmentDTO;
import com.juvarya.nivaas.customer.dto.JTPrePaidMeterDTO;
import com.juvarya.nivaas.customer.model.PrepaidMeterModel;
import com.juvarya.nivaas.utils.converter.Populator;

@Component
public class PrePaidMeterPopulator implements Populator<PrepaidMeterModel, JTPrePaidMeterDTO> {

	@Autowired
	private ApartmentPopulator apartmentPopulator;

	@Override
	public void populate(PrepaidMeterModel source, JTPrePaidMeterDTO target) {
		target.setId(source.getId());
		target.setCreationTime(source.getCreationTime());
		target.setCostPerUnit(source.getCostPerUnit());
		target.setDescription(source.getDescription());
		target.setName(source.getName());

		if (null != source.getApartmentModel()) {
			ApartmentDTO apartmentDTO = new ApartmentDTO();
			apartmentPopulator.populate(source.getApartmentModel(), apartmentDTO);
			target.setApartmentId(source.getApartmentModel().getId());
			target.setApartmentDTO(apartmentDTO);
		}

	}

}
