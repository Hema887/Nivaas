package com.juvarya.nivaas.core.populator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.juvarya.nivaas.commonservice.dto.AddressDTO;
import com.juvarya.nivaas.commonservice.dto.NivaasCityDTO;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.core.client.AccessMgmtClient;
import com.juvarya.nivaas.core.model.AddressModel;
import com.juvarya.nivaas.utils.converter.Populator;

@Component
public class AddressPopulator implements Populator<AddressModel, AddressDTO> {
	@Autowired
	private NivaasCityPopulator nivaasCityPopulator;

	@Autowired
	private AccessMgmtClient accessMgmtClient;

	@Override
	public void populate(AddressModel source, AddressDTO target) {
		target.setId(source.getId());
		target.setCreationTime(source.getCreationTime());
		target.setLine1(source.getLine1());
		target.setLine2(source.getLine2());
		target.setLine3(source.getLine3());
		target.setLocality(source.getLocality());
		target.setPostalCode(source.getPostalCode());

		if (source.getCity() != null) {
			NivaasCityDTO cityDTO = new NivaasCityDTO();
			nivaasCityPopulator.populate(source.getCity(), cityDTO);
			target.setCityId(source.getCity().getId());
			target.setNivaasCityDTO(cityDTO);
		}

		UserDTO userDTO = new UserDTO();
		if (source.getCreatedBy() != null) {
			UserDTO user = accessMgmtClient.getUserById(source.getCreatedBy());
			userDTO.setId(user.getId());
			userDTO.setFullName(user.getFullName());
			userDTO.setPrimaryContact(user.getPrimaryContact());
			target.setCreatedById(user.getId());
			target.setCreatedBy(userDTO);
		}
	}

}
