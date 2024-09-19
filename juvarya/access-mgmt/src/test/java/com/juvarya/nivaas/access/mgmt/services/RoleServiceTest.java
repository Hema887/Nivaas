package com.juvarya.nivaas.access.mgmt.services;

import com.juvarya.nivaas.access.mgmt.AccessBaseTest;
import com.juvarya.nivaas.access.mgmt.model.Role;
import com.juvarya.nivaas.commonservice.enums.ERole;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.when;

import java.util.Optional;

class RoleServiceTest extends AccessBaseTest {

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        init();
    }

    @Test
    void testFindByErole() {
        // Given
        ERole expectedRole = ERole.ROLE_APARTMENT_ADMIN;
        Role role = new Role();
        role.setName(expectedRole);
        
        // Save the role in the repository
        roleRepository.save(role);

        // When
        Role foundRole = roleService.findByErole(expectedRole);

        // Then
        assertNotNull(foundRole, "Role should not be null");
        assertEquals(expectedRole, foundRole.getName(), "Role names should match");
    }
}
