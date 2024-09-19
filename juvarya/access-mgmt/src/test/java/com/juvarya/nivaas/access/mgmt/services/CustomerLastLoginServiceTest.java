package com.juvarya.nivaas.access.mgmt.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockitoAnnotations;

import com.juvarya.nivaas.access.mgmt.AccessBaseTest;
import com.juvarya.nivaas.access.mgmt.model.CustomerLastLoginModel;
import com.juvarya.nivaas.access.mgmt.model.User;

class CustomerLastLoginServiceTest extends AccessBaseTest {

    @BeforeEach
    void setUp() {
        super.init(); 
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testSaveAndFindByJtCustomer() {
        // Create and save a User entity first
        User user = new User();
        user.setUsername("testUser");
        user.setPrimaryContact("1234567890"); // Set a valid primaryContact value
        userRepository.save(user); // Ensure user is persisted in the database

        // Now create CustomerLastLoginModel and set the user
        CustomerLastLoginModel model = new CustomerLastLoginModel();
        model.setCustomer(user);

        // Call the service method
        CustomerLastLoginModel savedModel;
        try {
            savedModel = customerLastLoginService.save(model);
        } catch (Exception e) {
            fail("Exception occurred during save: " + e.getMessage());
            return;
        }

        // Assertions and verifications
        assertNotNull(savedModel);
        assertNotNull(savedModel.getId()); // Verify ID is generated
        assertEquals(model.getCustomer().getId(), savedModel.getCustomer().getId()); // Verify user ID in saved model

        // Optionally, you can retrieve the saved model from repository and further assert
        CustomerLastLoginModel retrievedModel = customerLastLoginRepository.findById(savedModel.getId()).orElse(null);
        assertNotNull(retrievedModel);
        assertEquals(savedModel.getId(), retrievedModel.getId());
        assertEquals(savedModel.getCustomer().getId(), retrievedModel.getCustomer().getId());
    }
}
