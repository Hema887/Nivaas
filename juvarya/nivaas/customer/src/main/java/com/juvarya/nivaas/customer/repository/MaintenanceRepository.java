package com.juvarya.nivaas.customer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.customer.model.MaintenanceModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;

@Repository
public interface MaintenanceRepository extends JpaRepository<MaintenanceModel, Long> {

	MaintenanceModel findByApartmentModel(NivaasApartmentModel nivaasApartmentModel);

	List<MaintenanceModel> findByNotifyOnIn(List<Integer> notifyOnList);

	@Query("select a from MaintenanceModel a where a.apartmentModel.id = :apartmentId")
	Optional<MaintenanceModel> findByApartmentId(@Param("apartmentId") final Long apartmentId);

}
