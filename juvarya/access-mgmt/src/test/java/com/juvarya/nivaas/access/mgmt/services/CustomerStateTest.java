package com.juvarya.nivaas.access.mgmt.services;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.juvarya.nivaas.access.mgmt.AccessBaseTest;
import com.juvarya.nivaas.auth.JwtUtils;
import com.juvarya.nivaas.commonservice.dto.LoggedInUser;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.servlet.http.HttpServletRequest;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class CustomerStateTest extends AccessBaseTest {

   
	@BeforeEach
    void setUp() {
        super.init();
    }


    @Test
    void testGetLoggedInUser() throws JsonProcessingException {
        // Scenario 1: Valid Token
        String jwtValid = "validToken";
        HttpServletRequest requestValid = mockHttpServletRequest("Bearer " + jwtValid);
        LoggedInUser loggedInUserValid = new LoggedInUser();
        loggedInUserValid.setFullName("testuser");

        when(jwtUtils.validateJwtToken(jwtValid)).thenReturn(true);
        when(jwtUtils.getUserFromToken(jwtValid)).thenReturn(loggedInUserValid);

        LoggedInUser resultValid = customerState.getLoggedInUser(requestValid);

        assertNotNull(resultValid);
        assertEquals("testuser", resultValid.getFullName());

        // Scenario 2: Invalid Token
        String jwtInvalid = "invalidToken";
        HttpServletRequest requestInvalid = mockHttpServletRequest("Bearer " + jwtInvalid);

        when(jwtUtils.validateJwtToken(jwtInvalid)).thenReturn(false);

        LoggedInUser resultInvalid = customerState.getLoggedInUser(requestInvalid);

        assertNull(resultInvalid);

        // Scenario 3: Null Token
        HttpServletRequest requestNull = mockHttpServletRequest(null);

        LoggedInUser resultNull = customerState.getLoggedInUser(requestNull);

        assertNull(resultNull);
    }

    private HttpServletRequest mockHttpServletRequest(String authorizationHeader) {
        HttpServletRequest request = Mockito.mock(HttpServletRequest.class);
        if (authorizationHeader != null) {
            when(request.getHeader("Authorization")).thenReturn(authorizationHeader);
        }
        return request;
    }
}
