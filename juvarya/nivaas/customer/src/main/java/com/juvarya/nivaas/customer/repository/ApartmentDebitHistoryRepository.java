package com.juvarya.nivaas.customer.repository;

import com.juvarya.nivaas.customer.model.ApartmentDebitHistoryModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ApartmentDebitHistoryRepository extends JpaRepository<ApartmentDebitHistoryModel, Long> {

    @Query("SELECT a FROM ApartmentDebitHistoryModel a WHERE a.apartmentModel.id = :apartmentId AND" +
            " YEAR(a.transactionDate) = :year AND MONTH(a.transactionDate) = :month")
    List<ApartmentDebitHistoryModel> findByApartmentIdAndYearAndMonth(@Param("apartmentId") Long apartmentId,
                                                                      @Param("year") int year,
                                                                      @Param("month") int month);
}
