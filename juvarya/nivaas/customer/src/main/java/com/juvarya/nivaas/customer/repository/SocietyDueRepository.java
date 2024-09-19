package com.juvarya.nivaas.customer.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.juvarya.nivaas.customer.model.SocietyDue;

@Repository
public interface SocietyDueRepository extends JpaRepository<SocietyDue, Long> {

	@Query("SELECT a FROM SocietyDue a WHERE a.apartmentId = :apartmentId AND" +
			" YEAR(a.dueDate) = :year AND MONTH(a.dueDate) = :month")
	List<SocietyDue> findByApartmentIdAndYearAndMonth(@Param("apartmentId") Long apartmentId,
                                                      @Param("year") int year,
                                                      @Param("month") int month);

	@Query("SELECT s FROM SocietyDue s WHERE s.apartmentId = :apartmentId AND s.flatId = :flatId AND YEAR(s.dueDate) = :year AND MONTH(s.dueDate) = :month")
	Optional<SocietyDue> getSocietyDues(@Param("apartmentId") Long apartmentId, @Param("flatId") Long flatId,
                                        @Param("year") int year, @Param("month") int month);
 
	 @Query("SELECT s FROM SocietyDue s WHERE s.apartmentId = :apartmentId AND YEAR(s.dueDate) = :year AND MONTH(s.dueDate) = :month")
	    List<SocietyDue> getAllSocietyDues(@Param("apartmentId") Long apartmentId,
                                           @Param("year") int year,
                                           @Param("month") int month);
	 
	 List<SocietyDue> findByApartmentId(Long apartmentId);
}
