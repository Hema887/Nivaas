package com.juvarya.nivaas.customer.repository;

import com.juvarya.nivaas.customer.model.CurrentApartment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CurrentApartmentRepository extends JpaRepository<CurrentApartment, Long> {

    Optional<CurrentApartment> findByUserId(Long userId);

    @Modifying
    @Query("DELETE FROM CurrentApartment c WHERE c.userId = :userId")
    void deleteByUserId(@Param("userId") Long userId);
}

