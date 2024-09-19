package com.juvarya.nivaas.customer.populator;

import org.springframework.stereotype.Component;

import com.juvarya.nivaas.commonservice.dto.JTUserDTO;
import com.juvarya.nivaas.customer.dto.VehicleDTO;
import com.juvarya.nivaas.customer.model.VehicleModel;
import com.juvarya.nivaas.customer.model.constants.VehicleType;
import com.juvarya.nivaas.utils.converter.Populator;

@Component
public class VehiclePopulator implements Populator<VehicleModel, VehicleDTO> {

	private static final String ThreeWheeler = "ThreeWheeler";
	private static final String TwoWheeler = "TwoWheeler";
	private static final String FourWheeler = "FourWheeler";

	@Override
	public void populate(VehicleModel source, VehicleDTO target) {
		target.setId(source.getId());
		target.setBrand(source.getBrand());
		target.setColor(source.getColor());
		target.setVehicleNumber(source.getVehicleNumber());
		
		if (null != source.getVehicleType()) {
			if (source.getVehicleType().equals(VehicleType.TwoWheeler)) {
				target.setVehicleType(TwoWheeler);
			} else if (source.getVehicleType().equals(VehicleType.ThreeWheeler)) {
				target.setVehicleType(ThreeWheeler);
			} else if (source.getVehicleType().equals(VehicleType.FourWheeler)) {
				target.setVehicleType(FourWheeler);
			}
		 
		JTUserDTO user = new JTUserDTO();
		if (null != source.getCustomerId()) {
			target.setCustomerId(source.getCustomerId());
			target.setCustomerDto(user);
		}

		}
	}
}
