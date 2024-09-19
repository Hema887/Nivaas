package com.juvarya.nivaas.access.mgmt.controllers;

import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.juvarya.nivaas.access.mgmt.AbstractControllerTest;
import com.juvarya.nivaas.access.mgmt.dto.UserOTPDTO;
import com.juvarya.nivaas.access.mgmt.model.UserOTPModel;

class UserOTPEndpointTest extends AbstractControllerTest {

    private ObjectMapper objectMapper = new ObjectMapper();

    @Test
    @WithMockUser(username = "testuser", roles = {"USER"})
    void testSaveJTUserOTP_AllScenarios() throws Exception {
        // Mocked DTO
        UserOTPDTO userOTPDTO = new UserOTPDTO();
        userOTPDTO.setPrimaryContact("1111111111");
        userOTPDTO.setOtpType("EMAIL");

        // Scenario 1: Success - save OTP
        Mockito.lenient().when(userOTPService.save(any(UserOTPModel.class))).thenReturn(new UserOTPModel());

        // Convert DTO to JSON
        String userOTPDTOJson = objectMapper.writeValueAsString(userOTPDTO);

        // Perform POST request and verify
        mockMvc.perform(post("/nivaas/auth/jtuserotp/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(userOTPDTOJson))
                .andExpect(status().isOk());

        // Scenario 2: Success - No existing OTPs
        List<UserOTPModel> emptyOtpModels = new ArrayList<>();
        Mockito.lenient().when(userOTPService.findByPrimaryContactAndOtpType(any(String.class), any(String.class)))
                .thenReturn(emptyOtpModels);

        UserOTPModel userOTPModel2 = new UserOTPModel();
        userOTPModel2.setPrimaryContact("1111111111");
        userOTPModel2.setOtp("123456");

        Mockito.lenient().when(userOTPService.save(any(UserOTPModel.class))).thenReturn(userOTPModel2);

        // Perform the POST request and verify
        mockMvc.perform(post("/nivaas/auth/jtuserotp/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userOTPDTO)))
                .andExpect(status().isOk());

        // Scenario 3: Existing OTPs found and deleted
        UserOTPModel existingOTP = new UserOTPModel();
        existingOTP.setId(1L);

        List<UserOTPModel> userOTPModels = Collections.singletonList(existingOTP);
        Mockito.lenient().when(userOTPService.findByPrimaryContactAndOtpType(any(String.class), any(String.class)))
                .thenReturn(userOTPModels);

        Mockito.lenient().doNothing().when(userOTPService).deleteOTP(existingOTP);

        Mockito.lenient().when(userOTPService.save(any(UserOTPModel.class))).thenReturn(userOTPModel2);

        // Perform the POST request and verify
        mockMvc.perform(post("/nivaas/auth/jtuserotp/trigger")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(userOTPDTO)))
                .andExpect(status().isOk());
    }
}
