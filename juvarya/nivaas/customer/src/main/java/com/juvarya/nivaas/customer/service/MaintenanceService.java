package com.juvarya.nivaas.customer.service;

import java.util.List;

import org.springframework.http.ResponseEntity;

import com.juvarya.nivaas.customer.dto.JTMaintenanceDTO;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.PrepaidMeterModel;

@SuppressWarnings("rawtypes")
public interface MaintenanceService {

	List<PrepaidMeterModel> getPrepaid(NivaasApartmentModel apartment);

	ResponseEntity create(JTMaintenanceDTO jtMaintenanceDTO);

	JTMaintenanceDTO findByApartment(Long apartmentId)
			throws ClassNotFoundException, InstantiationException, IllegalAccessException;

}
