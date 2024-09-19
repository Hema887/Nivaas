package com.juvarya.nivaas.access.mgmt.populator;

import com.juvarya.nivaas.access.mgmt.model.MediaModel;
import com.juvarya.nivaas.commonservice.dto.MediaDTO;
import com.juvarya.nivaas.utils.converter.Populator;
import org.springframework.stereotype.Component;

@Component
public class MediaPopulator implements Populator<MediaModel, MediaDTO> {

	@Override
	public void populate(MediaModel source, MediaDTO target) {
		target.setId(source.getId());
		target.setName(source.getName());
		target.setUrl(source.getUrl());
		target.setDecsription(source.getDescription());
		target.setExtension(source.getExtension());

	}

}
