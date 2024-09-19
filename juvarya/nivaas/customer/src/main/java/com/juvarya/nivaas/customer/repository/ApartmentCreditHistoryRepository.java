package com.juvarya.nivaas.customer.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.customer.model.ApartmentCreditHistoryModel;

@Repository
public interface ApartmentCreditHistoryRepository extends JpaRepository<ApartmentCreditHistoryModel, Long> {

	@Query("SELECT a FROM ApartmentCreditHistoryModel a WHERE a.apartmentModel.id =:apartmentId AND"
			+ " YEAR(a.transactionDate) =:year AND MONTH(a.transactionDate) =:month")
	List<ApartmentCreditHistoryModel> findByApartmentIdAndYearAndMonth(@Param("apartmentId") Long apartmentId,
			@Param("year") int year, @Param("month") int month);
}
