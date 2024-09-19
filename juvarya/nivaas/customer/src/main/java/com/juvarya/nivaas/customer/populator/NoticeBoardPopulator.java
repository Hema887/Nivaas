package com.juvarya.nivaas.customer.populator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.juvarya.nivaas.commonservice.dto.ApartmentDTO;
import com.juvarya.nivaas.customer.dto.NoticeBoardDTO;
import com.juvarya.nivaas.customer.model.NoticeBoardModel;
import com.juvarya.nivaas.utils.converter.Populator;

@Component
public class NoticeBoardPopulator implements Populator<NoticeBoardModel, NoticeBoardDTO> {

	@Autowired
	private ApartmentPopulator apartmentPopulator;

	@Override
	public void populate(NoticeBoardModel source, NoticeBoardDTO target) {
		target.setId(source.getId());
		target.setTitle(source.getTitle());
		target.setBody(source.getBody());
		target.setPublishTime(source.getPublishTime());

		if (null != source.getApartment()) {
			ApartmentDTO apartmentDTO = new ApartmentDTO();
			apartmentPopulator.populate(source.getApartment(), apartmentDTO);
			target.setApartmentDTO(apartmentDTO);
			target.setApartmentId(source.getApartment().getId());
		}

	}
}
