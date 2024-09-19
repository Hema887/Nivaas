package com.juvarya.nivaas.customer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;

@Repository
public interface NivaasFlatRepository extends JpaRepository<NivaasFlatModel, Long> {

	@Query("SELECT CASE WHEN COUNT(f) > 0 THEN true ELSE false END " +
			"FROM NivaasFlatModel f " +
			"WHERE f.apartment.id = :apartmentId AND f.flatNo = :flatNo")
	boolean existsByApartmentIdAndFlatNo(@Param("apartmentId") Long apartmentId, @Param("flatNo") String flatNo);

	Page<NivaasFlatModel> findAll(Pageable pageable);

	@Query("SELECT flats FROM NivaasFlatModel flats WHERE flats.apartment.id = :apartmentId")
	Page<NivaasFlatModel> getFlatsByApartment(@Param("apartmentId") Long apartmentId, Pageable pageable);

	@Query("SELECT flats FROM NivaasFlatModel flats WHERE flats.apartment.id = :apartmentId")
	List<NivaasFlatModel> getAllFlatsByApartment(@Param("apartmentId") Long apartmentId);

	@Query("SELECT flat FROM NivaasFlatModel flat WHERE flat.ownerId = :user OR flat.tenantId = :user")
	List<NivaasFlatModel> findByOwnerORTenant(@Param("user") Long user);

	List<NivaasFlatModel> findByApartment(NivaasApartmentModel nivaasApartmentModel);

	@Query("SELECT flat FROM NivaasFlatModel flat WHERE flat.apartment.id = :apartmentId AND flat.id = :flatId")
	Optional<NivaasFlatModel> findByApartmentAndFlatId(@Param("apartmentId") Long apartmentId, @Param("flatId") Long flatId);

	@Query("SELECT flat FROM NivaasFlatModel flat WHERE flat.ownerId = :userId AND flat.id != :flatId")
	List<NivaasFlatModel> findByOwnerNotInFlat(@Param("userId") Long userId, @Param("flatId") Long flatId);
	
	@Query("SELECT flat FROM NivaasFlatModel flat WHERE flat.ownerId = :user OR flat.tenantId = :user")
	Page<NivaasFlatModel> getByOwnerORTenant(@Param("user") Long user, Pageable pageable);

}
