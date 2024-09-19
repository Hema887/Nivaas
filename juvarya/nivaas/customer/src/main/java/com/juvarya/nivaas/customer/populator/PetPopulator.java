package com.juvarya.nivaas.customer.populator;

import org.springframework.stereotype.Component;

import com.juvarya.nivaas.commonservice.dto.JTUserDTO;
import com.juvarya.nivaas.customer.dto.PetDTO;
import com.juvarya.nivaas.customer.model.PetModel;
import com.juvarya.nivaas.customer.model.constants.PetType;
import com.juvarya.nivaas.utils.converter.Populator;

@Component
public class PetPopulator implements Populator<PetModel, PetDTO> {

	private static final String BIRD = "JTPetType";
	private static final String DOG = "DOG";

	@Override
	public void populate(PetModel source, PetDTO target) {
		target.setId(source.getId());
		target.setColour(source.getColour());
		target.setNickName(source.getNickName());
		target.setBreed(source.getBreed());
		target.setPetType(null);

		if (null != source.getPetType()) {
			if (source.getPetType().equals(PetType.BIRD)) {
				target.setPetType(BIRD);
			} else if (source.getPetType().equals(PetType.CAT)) {
				target.setPetType(BIRD);
			} else if (source.getPetType().equals(PetType.DOG)) {
				target.setPetType(DOG);
			}

		}

		JTUserDTO userDTO = new JTUserDTO();
		if (null != source.getCustomerId()) {
			target.setCustomerId(source.getCustomerId());
			target.setCustomerDto(userDTO);

		}
	}
}
