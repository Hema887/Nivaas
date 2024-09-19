package com.juvarya.nivaas.core.populator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.juvarya.nivaas.commonservice.dto.NivaasCityDTO;
import com.juvarya.nivaas.commonservice.dto.PostalCodeDTO;
import com.juvarya.nivaas.core.model.NivaasCityModel;
import com.juvarya.nivaas.core.model.PostalCodeModel;
import com.juvarya.nivaas.utils.converter.Populator;

@Component
public class NivaasCityPopulator implements Populator<NivaasCityModel, NivaasCityDTO> {
	@Autowired
	private PostalCodePopulator postalCodePopulator;

	@Override
	public void populate(NivaasCityModel source, NivaasCityDTO target) {
		target.setId(source.getId());
		target.setName(source.getName());
		target.setCreationTime(source.getCreationTime());
		target.setIsoCode(source.getIsoCode());
		target.setCountry(source.getCountry());
		target.setRegion(source.getRegion());
		target.setDistrict(source.getDistrict());

		if (!CollectionUtils.isEmpty(source.getPostalCodes())) {
			List<String> codes = new ArrayList<>();
			for (PostalCodeModel codeModel : source.getPostalCodes()) {
				PostalCodeDTO codeDTO = new PostalCodeDTO();
				postalCodePopulator.populate(codeModel, codeDTO);
				codes.add(codeDTO.getCode());
			}
			target.setCodes(codes);
		}
	}

}
