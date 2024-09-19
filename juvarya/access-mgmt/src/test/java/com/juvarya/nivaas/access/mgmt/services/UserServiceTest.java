package com.juvarya.nivaas.access.mgmt.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.Optional;

import com.juvarya.nivaas.access.mgmt.AccessBaseTest;
import com.juvarya.nivaas.access.mgmt.model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class UserServiceTest extends AccessBaseTest {

    @BeforeEach
    void setUp() {
        super.init();
    }
    @Test
    public void testUserServiceOperations() {
        // Given: Prepare a new user
        User newUser = new User();
        newUser.setPrimaryContact("2222222222");
        newUser.setEmail("newuser@example.com");

        // When: Save the new user
        User savedUser = userService.saveUser(newUser);

        // Then: Verify the saved user
        assertNotNull(savedUser, "Saved user should not be null");
        assertEquals(newUser.getEmail(), savedUser.getEmail(), "User emails should match");
        assertEquals(newUser.getPrimaryContact(), savedUser.getPrimaryContact(), "User primary contacts should match");

        // Given: Prepare another user for further tests
        User user = buildUser();
        userService.saveUser(user);

        // When: Find user by primary contact
        Optional<User> retrievedUserByPrimaryContact = userService.findByPrimaryContact(user.getPrimaryContact());

        // Then: Verify the retrieved user by primary contact
        assertTrue(retrievedUserByPrimaryContact.isPresent(), "User should be present");
        assertEquals(user.getId(), retrievedUserByPrimaryContact.get().getId(), "User IDs should match");

        // When: Find user by ID
        User retrievedUserById = userService.findById(user.getId());

        // Then: Verify the retrieved user by ID
        assertEquals(user.getId(), retrievedUserById.getId(), "User IDs should match");
        assertEquals(user.getEmail(), retrievedUserById.getEmail(), "User emails should match");

        // When: Find user by email
        User retrievedUserByEmail = userService.findByEmail(user.getEmail());

        // Then: Verify the retrieved user by email
        assertEquals(user.getId(), retrievedUserByEmail.getId(), "User IDs should match");
        assertEquals(user.getPrimaryContact(), retrievedUserByEmail.getPrimaryContact(), "User primary contacts should match");
    }

}