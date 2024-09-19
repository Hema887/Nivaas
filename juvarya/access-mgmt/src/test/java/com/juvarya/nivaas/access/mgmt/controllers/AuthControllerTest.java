package com.juvarya.nivaas.access.mgmt.controllers;

import com.juvarya.nivaas.access.mgmt.AbstractControllerTest;
import com.juvarya.nivaas.access.mgmt.dto.request.LoginRequest;
import com.juvarya.nivaas.access.mgmt.model.Role;
import com.juvarya.nivaas.access.mgmt.model.User;
import com.juvarya.nivaas.access.mgmt.model.UserOTPModel;
import com.juvarya.nivaas.auth.JwtUtils;
import com.juvarya.nivaas.commonservice.dto.LoggedInUser;
import com.juvarya.nivaas.commonservice.enums.ERole;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.Date;
import java.util.HashSet;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class AuthControllerTest extends AbstractControllerTest {

    @Autowired
    protected JwtUtils jwtUtils;

    @BeforeEach
    public void init() {
        super.init();
        ReflectionTestUtils.setField(authController, "jwtUtils", jwtUtils);
    }

    @Test
    void testAuthControllerMethods() throws Exception {
        // Test case for bad request when no content is provided
        mockMvc.perform(MockMvcRequestBuilders.post("/nivaas/auth/signin")
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(MockMvcResultMatchers.status().isBadRequest());

        // Test case for successful authentication
        LoginRequest loginRequestSuccess = new LoginRequest();
        loginRequestSuccess.setPrimaryContact("validPrimaryContact");
        loginRequestSuccess.setOtp("validOtp");

        UserOTPModel userOTPModel = new UserOTPModel();
        userOTPModel.setOtp("validOtp");
        userOTPModel.setPrimaryContact("validPrimaryContact");
        userOTPModel.setCreationTime(new Date());

        // Mock service responses for successful authentication
        Mockito.when(userOTPService.findByOtpTypeAndPrimaryContact(anyString(), anyString()))
                .thenReturn(userOTPModel);

        User user = new User();
        user.setPrimaryContact("validPrimaryContact");
        user.setEmail("test@test.com");
        Role roleUser = new Role();
        roleUser.setId(1L);
        roleUser.setName(ERole.ROLE_USER);
        user.setRoles(new HashSet<>(Collections.singleton(roleUser)));

        Mockito.when(userDetailsService.loadUserByPrimaryContact(anyString())).thenReturn(user);

        LoggedInUser loggedInUser = new LoggedInUser();
        loggedInUser.setPrimaryContact("validPrimaryContact");
        loggedInUser.setEmail("test@test.com");
        loggedInUser.setRoles(new HashSet<>(Collections.singletonList("ROLE_USER")));

        // Perform the request
        MvcResult result = mockMvc.perform(MockMvcRequestBuilders.post("/nivaas/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestSuccess)))
                .andDo(print()) // Print the response for debugging
                .andExpect(status().isOk())
                .andReturn();

        // Extract the response content
        String responseContent = result.getResponse().getContentAsString();
        System.out.println("Response Content: " + responseContent);

        // Assert the presence of expected fields in the response
        assertThat(responseContent).contains("\"token\"");
        assertThat(responseContent).contains("\"primaryContact\"");
        assertThat(responseContent).contains("\"email\"");
        assertThat(responseContent).contains("\"roles\"");
        assertThat(responseContent).contains("\"refreshToken\"");
        
        // Test case for invalid OTP
        Mockito.when(userOTPService.findByOtpTypeAndPrimaryContact(anyString(), anyString()))
                .thenReturn(null); // Simulating invalid OTP response

        mockMvc.perform(MockMvcRequestBuilders.post("/nivaas/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestSuccess)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest()) // Expecting status 400 for invalid OTP
                .andExpect(jsonPath("$.message").value("Unable to find otp"));

        // Test case for expired OTP
        userOTPModel.setCreationTime(new Date(System.currentTimeMillis() - 30 * 60 * 1000)); // 30 minutes ago

        Mockito.when(userOTPService.findByOtpTypeAndPrimaryContact(anyString(), anyString()))
                .thenReturn(userOTPModel); // Simulating expired OTP response

        MvcResult result1 = mockMvc.perform(MockMvcRequestBuilders.post("/nivaas/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestSuccess)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        String responseContent1 = result1.getResponse().getContentAsString();
        assertThat(responseContent1).containsIgnoringCase("OTP expired");
        
        // Test case for missing primary contact
        loginRequestSuccess.setPrimaryContact(null);

        // Perform the request
        MvcResult mvcResult = mockMvc.perform(MockMvcRequestBuilders.post("/nivaas/auth/signin")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(loginRequestSuccess)))
                .andExpect(MockMvcResultMatchers.status().isBadRequest())
                .andReturn();

        // Extract the response body
        String responseBody = mvcResult.getResponse().getContentAsString();
        System.out.println("Response Body: " + responseBody);

 
    }
}
