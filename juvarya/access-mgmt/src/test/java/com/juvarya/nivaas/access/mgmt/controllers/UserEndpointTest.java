package com.juvarya.nivaas.access.mgmt.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juvarya.nivaas.access.mgmt.AbstractControllerTest;
import com.juvarya.nivaas.access.mgmt.model.Role;
import com.juvarya.nivaas.access.mgmt.model.User;
import com.juvarya.nivaas.commonservice.dto.BasicOnboardUserDTO;
import com.juvarya.nivaas.commonservice.enums.ERole;

class UserEndpointTest extends AbstractControllerTest {

    @Test
    @WithMockUser(username = "testUser", roles = {"USER"})
    void testUserEndpointMethods() throws Exception {
        Long userId = 1L;
        String primaryContact = "1234567890";
        ERole userRole = ERole.ROLE_USER;

        // Mock data setup for findById
        User user = new User();
        user.setId(1L);
        user.setFullName("John Doe");
        user.setUsername("john.doe@example.com");
        Role apartmentAdmin = new Role();
        apartmentAdmin.setId(1L);
        apartmentAdmin.setName(ERole.ROLE_APARTMENT_ADMIN);
        user.setRoles(Set.of(apartmentAdmin));
        when(userService.findById(userId)).thenReturn(user);

        // Test findById
        mockMvc.perform(MockMvcRequestBuilders.get("/user/find/{userId}", userId)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk())
                .andExpect(MockMvcResultMatchers.jsonPath("$.id").value(userId));

        // Test findByRolesContainsIgnoreCase
        String testRole = "user";
        int testPage = 0;
        int testPageSize = 10;
        List<User> users = new ArrayList<>();
        Page<User> userPage = new PageImpl<>(users, PageRequest.of(testPage, testPageSize), 0);
        when(userService.findByRolesContainsIgnoreCase(eq(testRole), any())).thenReturn(userPage);
        mockMvc.perform(MockMvcRequestBuilders.get("/user/list/{role}", testRole)
                .param("page", String.valueOf(testPage))
                .param("pageSize", String.valueOf(testPageSize))
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Test getAllUsers
        Page<User> usersPage = new PageImpl<>(Collections.emptyList(), PageRequest.of(0, 10), 0);
        when(userService.getAllUsers(any())).thenReturn(usersPage);

        // Creating a request with correct endpoint and parameters
        mockMvc.perform(MockMvcRequestBuilders.get("/user/list?pageNo=0&pageSize=10")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Test onBoardUser
       String onboardUserRequest = "{\r\n"
       		+ "  \"username\": \"john.doe@example.com\",\r\n"
       		+ "  \"email\": \"john.doe@example.com\",\r\n"
       		+ "  \"fullName\": \"John Doe\",\r\n"
       		+ "  \"primaryContact\": \"1234567890\",\r\n"
       		+ "  \"userRoles\": [\"ROLE_USER\"]\r\n"
       		+ "}\r\n"
       		+ "";

        // Perform the mock MVC request
        mockMvc.perform(MockMvcRequestBuilders.post("/user/onboard/user")
                .contentType(MediaType.APPLICATION_JSON)
                .content(onboardUserRequest))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Test saveUser
        User saveUser = new User();
        saveUser.setFullName("John Doe");
        when(userService.saveUser(any())).thenReturn(saveUser);
        mockMvc.perform(MockMvcRequestBuilders.post("/user/save")
                .contentType(MediaType.APPLICATION_JSON)
                .content(asJsonString(saveUser)))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Test removeUserRole
        mockMvc.perform(MockMvcRequestBuilders.delete("/user/contact/123456789/role/ROLE_USER")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());

        // Test findByPrimaryContact
        User userByPrimaryContact = new User();
        userByPrimaryContact.setId(userId);
        userByPrimaryContact.setFullName("John Doe");
        userByPrimaryContact.setPrimaryContact(primaryContact);
        userByPrimaryContact.setRoles(new HashSet<>());
        when(userService.findByPrimaryContact(primaryContact)).thenReturn(Optional.of(userByPrimaryContact));
        mockMvc.perform(MockMvcRequestBuilders.get("/user/find/" + primaryContact)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isOk());
    }

    // Helper method to convert object to JSON string
    private String asJsonString(final Object obj) {
        try {
            return new ObjectMapper().writeValueAsString(obj);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
