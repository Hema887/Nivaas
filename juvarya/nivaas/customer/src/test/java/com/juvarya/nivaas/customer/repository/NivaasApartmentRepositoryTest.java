package com.juvarya.nivaas.customer.repository;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.customer.service.NivaasApartmentService;
import com.juvarya.nivaas.customer.service.impl.NivaasApartmentServiceImpl;

public class NivaasApartmentRepositoryTest {

	 @Mock
    private NivaasApartmentService apartmentService; // Assuming service injects repository

    @Mock
    private NivaasApartmentRepository apartmentRepository;
    
    @InjectMocks
    private NivaasApartmentServiceImpl jtaApartmentServiceImpl;

    private UserDTO user;
    private NivaasApartmentModel apartment1;
    private NivaasApartmentModel apartment2;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this); // Initialize mocks

        user = new UserDTO();
        user.setId(1L);

        apartment1 = new NivaasApartmentModel();
        apartment1.setName("Apartment 1");
        apartment1.setCreatedBy(user.getId());

        apartment2 = new NivaasApartmentModel();
        apartment2.setName("Apartment 2");
        apartment2.setCreatedBy(user.getId());
    }

//    @Test
//    public void testGetAddressByApartments_shouldFindApartmentsByCityId() {
//        Long cityId = 10L;
//        List<NivaasApartmentModel> apartments = new ArrayList<>();
//        apartments.add(apartment1);
//        apartments.add(apartment2);
//        Page<NivaasApartmentModel> expectedPage = new PageImpl<>(apartments);
//
//        // Mock repository behavior to return expected Page
//        when(apartmentRepository.getAddressByApartments(eq(cityId), any(Pageable.class)))
//                .thenReturn(expectedPage); 
//
//        // Call the service method
//        Page<NivaasApartmentModel> actualPage = apartmentService.getAddressByApartments(cityId, Pageable.unpaged());
//
//        // Debugging points:
//        // - Check if actualPage is null after the service call
//        if (actualPage == null) {
//            // Investigate why apartmentService.getAddressByApartments is not returning a Page
//        } else {
//            // Assertions
//            assertFalse(actualPage.getContent().isEmpty(), "List of apartments should not be empty");
//            assertEquals(2, actualPage.getContent().size(), "Number of apartments should be 2");
//        }
//    }    // Test cases for findByCreatedBy

    @Test
    public void testFindByCreatedBy_shouldFindApartmentsByCreatedBy() {
        // Create test data
        UserDTO user = new UserDTO();
        user.setId(1L);

        NivaasApartmentModel apartment1 = new NivaasApartmentModel();
        apartment1.setName("Apartment A");
        apartment1.setCreatedBy(user.getId());

        NivaasApartmentModel apartment2 = new NivaasApartmentModel();
        apartment2.setName("Apartment B");
        apartment2.setCreatedBy(user.getId());

        List<NivaasApartmentModel> apartments = new ArrayList<>();
        apartments.add(apartment1);
        apartments.add(apartment2);

        // Mock repository behavior
        when(apartmentRepository.findByCreatedBy(user.getId())).thenReturn(apartments);

        // Call service method
        List<NivaasApartmentModel> retrievedApartments = jtaApartmentServiceImpl.findByCreatedBy(user.getId());

        // Verify interaction with mocked repository
        verify(apartmentRepository).findByCreatedBy(user.getId());

        // Assertions
        assertFalse(retrievedApartments.isEmpty(), "List of apartments should not be empty");
        assertEquals(2, retrievedApartments.size(), "Number of apartments should be 2");
    }


    // Add more test cases for other methods of JTApartmentServiceImpl...

    
    @Test
    public void testFindByCreatedBy_shouldReturnEmptyListForNonexistentUser() {
        UserDTO nonExistentUser = new UserDTO();
        nonExistentUser.setId(100L); // Assuming User has an ID field

        when(apartmentRepository.findByCreatedBy(nonExistentUser.getId())).thenReturn(Collections.emptyList());

        List<NivaasApartmentModel> retrievedApartments = apartmentService.findByCreatedBy(nonExistentUser.getId());

        assertTrue(retrievedApartments.isEmpty(), "List should be empty for nonexistent user");
    }
}

