package com.juvarya.nivaas.access.mgmt.services;

import com.juvarya.nivaas.access.mgmt.model.Role;
import com.juvarya.nivaas.commonservice.enums.ERole;

public interface RoleService {
    Role findByErole(ERole eRole);
}
