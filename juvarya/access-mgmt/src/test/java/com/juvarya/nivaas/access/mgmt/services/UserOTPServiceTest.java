package com.juvarya.nivaas.access.mgmt.services;

import com.juvarya.nivaas.access.mgmt.model.UserOTPModel;
import com.juvarya.nivaas.access.mgmt.repository.UserOTPRepository;
import com.juvarya.nivaas.access.mgmt.services.impl.UserOTPServiceImpl;
import com.juvarya.nivaas.access.mgmt.AccessBaseTest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

class UserOTPServiceTest extends AccessBaseTest {

    @BeforeEach
    void setUp() {
        init(); // Initialize dependencies from AccessBaseTest
        MockitoAnnotations.openMocks(this);
    }
    @Test
    void testFindByPrimaryContactAndOtpType() {
        userOTPRepository.deleteAll();
        // Prepare test data
        String primaryContact = "1111111111";
        String otpType = "sms";
        UserOTPModel expectedModel = new UserOTPModel();
        expectedModel.setId(1L);
        expectedModel.setPrimaryContact(primaryContact);
        expectedModel.setOtpType(otpType);

        // Save the test data in the repository
        userOTPRepository.save(expectedModel);

        // Call the service method
        List<UserOTPModel> resultList = userOTPService.findByPrimaryContactAndOtpType(primaryContact, otpType);

        // Assert the result
        assertEquals(1, resultList.size());
        UserOTPModel foundModel = resultList.get(0);
        assertEquals(expectedModel.getPrimaryContact(), foundModel.getPrimaryContact());
        assertEquals(expectedModel.getOtpType(), foundModel.getOtpType());
    }
}