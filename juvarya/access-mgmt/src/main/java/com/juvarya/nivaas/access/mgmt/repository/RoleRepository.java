package com.juvarya.nivaas.access.mgmt.repository;

import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.access.mgmt.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    Optional<Role> findByName(ERole name);

    Set<Role> findByNameIn(Set<ERole> names);
}
