package com.juvarya.nivaas.customer.repository;

import com.juvarya.nivaas.customer.model.ApartmentAndFlatRelatedUsersModel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ApartmentAndFlatRelatedUsersModelRepository extends JpaRepository<ApartmentAndFlatRelatedUsersModel, Long> {
}
