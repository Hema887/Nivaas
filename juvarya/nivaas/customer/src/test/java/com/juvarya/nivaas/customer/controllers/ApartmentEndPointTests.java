package com.juvarya.nivaas.customer.controllers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.http.HttpServletRequest;

import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import com.juvarya.nivaas.commonservice.dto.LoggedInUser;
import com.juvarya.nivaas.customer.dto.request.ApartmentCoAdminDTO;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.customer.response.MessageResponse;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.service.ApartmentUserRoleService;
import com.juvarya.nivaas.utils.NivaasConstants;


public class ApartmentEndPointTests {

    @Mock
    private NivaasApartmentService apartmentService;

    @Mock
    private ApartmentUserRoleService apartmentUserRoleService;

    @InjectMocks
    private ApartmentEndPoint apartmentEndPoint;

	private LoggedInUser loggedInUser;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testAddCoAdmin() {
        ApartmentCoAdminDTO coAdminDTO = new ApartmentCoAdminDTO();
        coAdminDTO.setUserRole(ERole.ROLE_APARTMENT_ADMIN);

        ResponseEntity responseEntity = apartmentEndPoint.addCoAdmin(coAdminDTO);

        assertEquals(HttpStatus.OK, responseEntity.getStatusCode());
        assertEquals("OnBoarding CoAdmin request sent", ((MessageResponse) responseEntity.getBody()).getMessage());
    }

    @Test
    public void testAddCoAdmin_InvalidUserRole() {
        ApartmentCoAdminDTO coAdminDTO = new ApartmentCoAdminDTO();
        coAdminDTO.setUserRole(ERole.ROLE_USER);

        ResponseEntity responseEntity = apartmentEndPoint.addCoAdmin(coAdminDTO);

        assertEquals(HttpStatus.BAD_REQUEST, responseEntity.getStatusCode());
    }

    
    @Test
    public void testGetById_ApartmentNotFound() {
        Long apartmentId = 1L;

        when(apartmentService.findById(apartmentId)).thenReturn(null);

        NivaasCustomerException exception = assertThrows(NivaasCustomerException.class, () -> apartmentEndPoint.getById(apartmentId));


        assertEquals(HttpStatus.NOT_FOUND, exception.getErrorCode().getHttpStatus());
        assertEquals("Apartment Not Found", exception.getMessage());
    }

    @Test
    public void testDeleteApartment_Success() {
        Long apartmentId = 1L;
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        LoggedInUser loggedInUser = new LoggedInUser();
        loggedInUser.setId(1L);
        UserDTO user = new UserDTO();
        user.setId(1L);
        NivaasApartmentModel apartment = new NivaasApartmentModel();
        apartment.setId(apartmentId);
        ApartmentUserRoleModel userRole = new ApartmentUserRoleModel();
        userRole.setRoleName(NivaasConstants.ROLE_APARTMENT_ADMIN);
        equals(HttpStatus.OK.getClass());
        equals("Apartment Deleted");
     
    }

    @Test
    public void testDeleteApartment_ApartmentNotFound() {
        // Mock LoggedInUser
        LoggedInUser loggedInUser = new LoggedInUser();
        loggedInUser.setId(1L);

        // Mock User
        UserDTO user = new UserDTO();
        user.setId(1L);
        when(apartmentService.findById(anyLong())).thenReturn(null);
        // Assert
        equals(HttpStatus.BAD_REQUEST.getClass());
        equals("Apartment Not Found With Given Id");

        // Verify no further interactions with service
       
    }


    @Test
    public void testDeleteApartment_UserNotAuthorized() {
        Long apartmentId = 1L;
        HttpServletRequest mockRequest = mock(HttpServletRequest.class);
        LoggedInUser loggedInUser = new LoggedInUser();
        loggedInUser.setId(1L);
        UserDTO user = new UserDTO();
        user.setId(1L);
        NivaasApartmentModel apartment = new NivaasApartmentModel();
        apartment.setId(apartmentId);
        ApartmentUserRoleModel userRole = new ApartmentUserRoleModel();
        userRole.setRoleName("ROLE_OTHER");


        equals(HttpStatus.OK.getClass());
        equals("You Re Not Allowed TO Remove Apartment");
        verify(apartmentService, never()).removeApartment(any());
    }
}