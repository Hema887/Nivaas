package com.juvarya.nivaas.customer.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.customer.model.MaintenanceModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.PrepaidMeterModel;

@Repository
public interface PrepaidMeterRepository extends JpaRepository<PrepaidMeterModel, Long> {

	Page<PrepaidMeterModel> findByapartmentModel(NivaasApartmentModel nivaasApartmentModel, Pageable pageable);

	@Query("SELECT prepaid FROM PrepaidMeterModel prepaid WHERE prepaid.name=:name AND prepaid.apartmentModel.createdBy.id=:userId")
	PrepaidMeterModel findByNameAndApartmentModel(@Param("name") String name, @Param("userId") Long userId);

	List<PrepaidMeterModel> getByApartmentModel(NivaasApartmentModel nivaasApartmentModel);

	@Query("SELECT pre FROM PrepaidMeterModel pre WHERE pre.name =:name AND pre.apartmentModel.id =:apartmentId")
	PrepaidMeterModel getByNameAndApartmentModel(@Param("name") String name, @Param("apartmentId") Long apartmentId);

	long countByApartmentModel(NivaasApartmentModel nivaasApartmentModel);
	
	@Query("SELECT p FROM PrepaidMeterModel p WHERE p.maintenance.id = :maintenanceId")
    List<PrepaidMeterModel> findByMaintenanceId(@Param("maintenanceId") Long maintenanceId);

}
