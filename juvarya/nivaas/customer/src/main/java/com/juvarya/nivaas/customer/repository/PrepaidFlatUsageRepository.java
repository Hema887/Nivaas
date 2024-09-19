package com.juvarya.nivaas.customer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.customer.model.PrepaidFlatUsageModel;

@Repository
public interface PrepaidFlatUsageRepository extends JpaRepository<PrepaidFlatUsageModel, Long> {

	List<PrepaidFlatUsageModel> findByPrepaidMeterIdAndApartmentId(Long prepaidMeterId, Long apartmentId);

	Optional<PrepaidFlatUsageModel> findByFlatIdAndPrepaidMeterId(Long flatId, Long prepaidMeterId);

	List<PrepaidFlatUsageModel> findByFlatIdAndApartmentId(Long flatId, Long apartmentId);
	
	List<PrepaidFlatUsageModel> findByPrepaidMeterId(Long prepaidMeterId);
}
