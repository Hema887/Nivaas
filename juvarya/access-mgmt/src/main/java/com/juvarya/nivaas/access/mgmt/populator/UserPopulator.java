package com.juvarya.nivaas.access.mgmt.populator;

import com.juvarya.nivaas.commonservice.dto.JTUserDTO;
import com.juvarya.nivaas.access.mgmt.model.User;
import com.juvarya.nivaas.utils.converter.Populator;
import org.springframework.stereotype.Component;

@Component
public class UserPopulator implements Populator<User, JTUserDTO> {

	@Override
	public void populate(User source, JTUserDTO target) {
		target.setId(source.getId());
		target.setEmail(source.getEmail());
		target.setFullName(source.getFullName());
		target.setPrimaryContact(source.getPrimaryContact());
		if (null != source.getType()) {
			target.setType(source.getType());
		}

	}

}
