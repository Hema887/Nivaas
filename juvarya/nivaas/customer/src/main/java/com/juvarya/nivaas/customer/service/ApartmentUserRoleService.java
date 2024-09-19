package com.juvarya.nivaas.customer.service;

import java.util.List;

import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;

public interface ApartmentUserRoleService {

	ApartmentUserRoleModel onBoardApartmentAdminOrHelper(final NivaasApartmentModel nivaasApartmentModel, final Long adminUser, final String role);

	ApartmentUserRoleModel findByApproveCustomer(NivaasApartmentModel nivaasApartmentModel);

	List<ApartmentUserRoleModel> findByjtCustomerId(Long customerId);

	ApartmentUserRoleModel findByApartmentModelAndJtCustomer(NivaasApartmentModel nivaasApartmentModel, Long customerId);

	List<ApartmentUserRoleModel> getByApartmentModel(NivaasApartmentModel nivaasApartmentModele);

}
