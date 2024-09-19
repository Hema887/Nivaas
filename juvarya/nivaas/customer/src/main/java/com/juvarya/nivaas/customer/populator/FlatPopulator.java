package com.juvarya.nivaas.customer.populator;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.juvarya.nivaas.commonservice.dto.ApartmentDTO;
import com.juvarya.nivaas.commonservice.dto.FlatDTO;
import com.juvarya.nivaas.commonservice.dto.JTUserDTO;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.customer.client.AccessMgmtClient;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.utils.converter.Populator;

@Component
public class FlatPopulator implements Populator<NivaasFlatModel, FlatDTO> {

	@Autowired
	private ApartmentPopulator apartmentPopulator;

	@Autowired
	private AccessMgmtClient accessMgmtClient;

	@Override
	public void populate(NivaasFlatModel source, FlatDTO target) {
		target.setId(source.getId());
		target.setFlatNo(source.getFlatNo());
		target.setFacing(source.getFacing());
		target.setIsParkingAvailable(source.isParkingAvailable());
		target.setIsAvailableForSale(source.isAvailableForSale());
		target.setIsAvailableForRent(source.isAvailableForRent());
		target.setTotalRooms(source.getTotalRooms());
		target.setSquareFeet(source.getSquareFeet());

		ApartmentDTO apartmentDTO = new ApartmentDTO();
		if (null != source.getApartment()) {
			apartmentDTO.setId(source.getApartment().getId());
			apartmentDTO.setName(source.getApartment().getName());
			apartmentDTO.setTotalFlats(source.getApartment().getTotalFlats());

//			apartmentPopulator.populate(source.getApartment(), apartmentDTO);
//			target.setApartmentId(source.getApartment().getId());
			target.setApartmentDTO(apartmentDTO);
		}

		JTUserDTO ownerDTO = new JTUserDTO();
		if (null != source.getOwnerId()) {
			UserDTO user = accessMgmtClient.getUserById(source.getOwnerId());
			ownerDTO.setId(user.getId());
			ownerDTO.setFullName(user.getFullName());
			ownerDTO.setPrimaryContact(user.getPrimaryContact());
			target.setOwnerId(source.getOwnerId());
			target.setOwnerDTO(ownerDTO);

			JTUserDTO tenantDto = new JTUserDTO();
			if (null != source.getTenantId()) {
				UserDTO users = accessMgmtClient.getUserById(source.getOwnerId());
				tenantDto.setId(users.getId());
				tenantDto.setFullName(users.getFullName());
				tenantDto.setPrimaryContact(users.getPrimaryContact());
				target.setTenantId(source.getTenantId());
				target.setTenantDTO(tenantDto);
			}
		}
	}
}
