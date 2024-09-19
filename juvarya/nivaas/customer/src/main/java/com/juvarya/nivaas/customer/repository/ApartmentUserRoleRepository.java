package com.juvarya.nivaas.customer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;

@Repository
public interface ApartmentUserRoleRepository extends JpaRepository<ApartmentUserRoleModel, Long> {
	ApartmentUserRoleModel findByApartmentModel(NivaasApartmentModel nivaasApartmentModel);

	List<ApartmentUserRoleModel> findByCustomerId(Long customerId);

	ApartmentUserRoleModel findByApartmentModelAndCustomerId(NivaasApartmentModel nivaasApartmentModel, Long customerId);

	List<ApartmentUserRoleModel> getByApartmentModel(NivaasApartmentModel apartmentModel);
}
