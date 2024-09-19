package com.juvarya.nivaas.customer.repository;

import java.util.List;

import com.juvarya.nivaas.customer.model.constants.OnboardType;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.customer.model.OnboardingRequest;

@Repository
public interface OnboardingRequestRepository extends JpaRepository<OnboardingRequest, Long> {
	
	Page<OnboardingRequest> findByStatus(boolean status, Pageable pageable);

	Page<OnboardingRequest> findByFlat(NivaasFlatModel nivaasFlatModel, Pageable pageable);

	OnboardingRequest findByFlatAndAdminApproved(NivaasFlatModel nivaasFlatModel, boolean approve);

	@Query("SELECT DISTINCT o FROM OnboardingRequest o " +
			"LEFT JOIN o.relatedUsers ru " +
			"WHERE o.requestedCustomer = :user OR ru.userId = :user")
	List<OnboardingRequest> findByRequestedCustomer(@Param("user") Long user);

	@Query("SELECT DISTINCT o FROM OnboardingRequest o " +
			"LEFT JOIN o.relatedUsers ru " +
			"WHERE o.apartment.id = :apartmentId AND (o.requestedCustomer = :userId OR ru.userId = :userId)")
	List<OnboardingRequest> findByUserAndApartmentId(@Param("userId") Long userId, @Param("apartmentId") Long apartmentId);

	@Query("SELECT o FROM OnboardingRequest o " +
			"WHERE o.apartment.id = :apartmentId AND o.onboardType = :onboardType AND o.adminApproved = true")
	List<OnboardingRequest> findByApartmentAndAdminApprovedAndOnboardType(@Param("apartmentId") Long apartmentId,
																		  @Param("onboardType") OnboardType onboardType);

	@Query("SELECT o FROM OnboardingRequest o " +
			"WHERE o.flat.id = :flatId AND o.onboardType = :onboardType AND o.adminApproved = true")
	List<OnboardingRequest> findByFlatAndAdminApprovedAndOnboardType(@Param("flatId") Long flatId,
																	 @Param("onboardType") OnboardType onboardType);

	@Query("SELECT (COUNT(o) > 0) FROM OnboardingRequest o " +
			"LEFT JOIN o.relatedUsers ru " +
			"WHERE o.apartment.id = :apartmentId AND (o.requestedCustomer = :userId OR ru.userId = :userId)")
	Boolean existsByUserAndApartmentId(@Param("userId") Long userId, @Param("apartmentId") Long apartmentId);
	
	@Query("SELECT DISTINCT o FROM OnboardingRequest o " +
			"LEFT JOIN o.relatedUsers ru on o.id = ru.onboardingRequestId " +
			"WHERE o.apartment.id =:apartmentId and o.onboardType = 'FLAT'")
	List<OnboardingRequest> getAllFlatRequestsByApartmentId(@Param("apartmentId") Long apartmentId);
	
	Boolean existsByFlatAndOnboardTypeAndRequestedCustomer(NivaasFlatModel flatModel, OnboardType type, Long userId);

}
