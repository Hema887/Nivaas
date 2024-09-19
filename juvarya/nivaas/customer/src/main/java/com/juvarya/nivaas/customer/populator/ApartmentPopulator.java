package com.juvarya.nivaas.customer.populator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.juvarya.nivaas.commonservice.dto.AddressDTO;
import com.juvarya.nivaas.commonservice.dto.ApartmentDTO;
import com.juvarya.nivaas.customer.client.NivaasCoreClient;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.constants.ApartmentType;
import com.juvarya.nivaas.utils.NivaasConstants;
import com.juvarya.nivaas.utils.converter.Populator;

@Component
public class ApartmentPopulator implements Populator<NivaasApartmentModel, ApartmentDTO> {

	@Autowired
	private NivaasCoreClient nivaasCoreClient;

	@Override
	public void populate(NivaasApartmentModel source, ApartmentDTO target) {
		target.setId(source.getId());
		target.setName(source.getName());
		target.setApprove(source.isApprove());
		target.setCode(source.getCode());
		target.setDescription(source.getDescription());
		target.setTotalFlats(source.getTotalFlats());

		if (null != source.getApartmentType()) {
			if (source.getApartmentType().equals(ApartmentType.MULTIBLOCK)) {
				target.setApartmentType(NivaasConstants.MULTIBLOCK);
			} else if (source.getApartmentType().equals(ApartmentType.SINGLE)) {
				target.setApartmentType(NivaasConstants.SINGLE);
			}
		}
		target.setBuilderName(source.getBuilderName());
		if (null != source.getAddress()) {
			AddressDTO addressDTO = nivaasCoreClient.getById(source.getAddress());
			target.setAddressDTO(addressDTO);
		}
	}

}
