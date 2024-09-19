package com.juvarya.nivaas.core.populator;

import org.springframework.stereotype.Component;

import com.juvarya.nivaas.commonservice.dto.PostalCodeDTO;
import com.juvarya.nivaas.core.model.PostalCodeModel;
import com.juvarya.nivaas.utils.converter.Populator;

@Component
public class PostalCodePopulator implements Populator<PostalCodeModel, PostalCodeDTO> {

	@Override
	public void populate(PostalCodeModel source, PostalCodeDTO target) {
		target.setId(source.getId());
		target.setCreationTime(source.getCreationTime());
		target.setCode(source.getCode());
	}

}
