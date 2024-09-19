package com.juvarya.nivaas.access.mgmt.repository;

import com.juvarya.nivaas.access.mgmt.model.CustomerLastLoginModel;
import com.juvarya.nivaas.access.mgmt.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerLastLoginRepository extends JpaRepository<CustomerLastLoginModel, Long> {

	CustomerLastLoginModel findByCustomer(User user);
}
