package com.juvarya.nivaas.customer.populator;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import com.juvarya.nivaas.commonservice.dto.ApartmentDTO;
import com.juvarya.nivaas.customer.dto.JTMaintenanceDTO;
import com.juvarya.nivaas.customer.dto.JTPrePaidMeterDTO;
import com.juvarya.nivaas.customer.model.MaintenanceModel;
import com.juvarya.nivaas.customer.model.PrepaidMeterModel;
import com.juvarya.nivaas.utils.converter.Populator;

@Component
public class MaintenancePopulator implements Populator<MaintenanceModel, JTMaintenanceDTO> {
	@Autowired
	private PrePaidMeterPopulator meterPopulator;

	@Override
	public void populate(MaintenanceModel source, JTMaintenanceDTO target) {
		target.setId(source.getId());
		target.setCost(source.getCost());
		target.setCreationTime(source.getCreationTime());
		target.setNotifyOn(source.getNotifyOn());

		if (!CollectionUtils.isEmpty(source.getMeters())) {
			List<JTPrePaidMeterDTO> prePaidMeterDtos = new ArrayList<>();
			List<Long> prepaidMeters = new ArrayList<>();
			for (PrepaidMeterModel meterModel : source.getMeters()) {
				JTPrePaidMeterDTO jtPrePaidMeterDTO = new JTPrePaidMeterDTO();
				jtPrePaidMeterDTO.setId(meterModel.getId());
				jtPrePaidMeterDTO.setName(meterModel.getName());
				jtPrePaidMeterDTO.setApartmentId(meterModel.getApartmentModel().getId());

				ApartmentDTO apartmentDTO = new ApartmentDTO();
				apartmentDTO.setId(meterModel.getApartmentModel().getId());
				apartmentDTO.setName(meterModel.getApartmentModel().getName());
				jtPrePaidMeterDTO.setApartmentDTO(apartmentDTO);

//				meterPopulator.populate(meterModel, jtPrePaidMeterDTO);
				prePaidMeterDtos.add(jtPrePaidMeterDTO);
				prepaidMeters.add(jtPrePaidMeterDTO.getId());
			}
			target.setJtPrePaidMeterDTOs(prePaidMeterDtos);
			target.setPrepaidId(prepaidMeters);

		}

	}

}
