package com.juvarya.nivaas.access.mgmt.services.impl;

import com.juvarya.nivaas.access.mgmt.model.Role;
import com.juvarya.nivaas.access.mgmt.repository.RoleRepository;
import com.juvarya.nivaas.access.mgmt.services.RoleService;
import com.juvarya.nivaas.commonservice.enums.ERole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Override
    public Role findByErole(ERole eRole) {
        Optional<Role> role = roleRepository.findByName(eRole);
        return role.orElse(null);
    }
}
