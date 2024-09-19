package com.juvarya.nivaas.access.mgmt.controllers;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.juvarya.nivaas.access.mgmt.AbstractControllerTest;
import com.juvarya.nivaas.access.mgmt.model.Role;
import com.juvarya.nivaas.commonservice.enums.ERole;

class RoleControllerTest extends AbstractControllerTest {

    @Test
    @WithMockUser(username = "user", roles = {"USER"})
    void testGetByERole_ValidRole() throws Exception {
        // Scenario 1: Valid Role
        Role mockRole = new Role();
        mockRole.setId(1L);
        mockRole.setName(ERole.ROLE_USER);
        when(roleService.findByErole(ERole.ROLE_USER)).thenReturn(mockRole);

        mockMvc.perform(get("/role/find/ROLE_USER")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().json("{\"id\":1,\"name\":\"ROLE_USER\"}"));

        // Verify that roleService was called with expected argument
        verify(roleService).findByErole(ERole.ROLE_USER);
    }
}
