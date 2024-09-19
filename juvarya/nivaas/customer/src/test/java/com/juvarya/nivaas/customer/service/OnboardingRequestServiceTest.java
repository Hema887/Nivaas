package com.juvarya.nivaas.customer.service;

import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.commonservice.dto.OnboardingRequestDTO;
import com.juvarya.nivaas.customer.NivaasBaseTest;
import com.juvarya.nivaas.customer.dto.BulkFlatOnboardDto;
import com.juvarya.nivaas.customer.dto.FlatBasicDTO;
import com.juvarya.nivaas.customer.dto.response.CurrentApartmentDto;
import com.juvarya.nivaas.customer.model.ApartmentAndFlatRelatedUsersModel;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.NivaasFlatModel;
import com.juvarya.nivaas.customer.model.OnboardingRequest;
import com.juvarya.nivaas.customer.model.constants.OnboardType;
import com.juvarya.nivaas.customer.model.constants.RelatedType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;

import java.util.List;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class OnboardingRequestServiceTest extends NivaasBaseTest {

    @BeforeEach
    public void init() {
        super.init();
    }

    @AfterEach
    public void cleanUp() {
        super.cleanUp();
    }

    @Test
    void bulkFlatOnboardTest() {
        //add new apartment
        NivaasApartmentModel apartmentModel = saveTestApartment("Test Apartment");
        FlatBasicDTO flat1 = new FlatBasicDTO("101", "1234567890", "user1");
        FlatBasicDTO flat2 = new FlatBasicDTO("102", "1234567891", "user2");
        FlatBasicDTO flat3 = new FlatBasicDTO("102", "1234567891", "user3");

        //throws exception to onboard flats when apartment not found
        {
            BulkFlatOnboardDto bulkFlatOnboardDto = new BulkFlatOnboardDto(111L, List.of(flat1, flat2));
            NivaasCustomerException exception = assertThrows(NivaasCustomerException.class, () -> onboardingRequestService.bulkAdd(bulkFlatOnboardDto));
            assertEquals(HttpStatus.NOT_FOUND, exception.getErrorCode().getHttpStatus());
            assertEquals("Apartment Not Found", exception.getMessage());
        }
        //throws exception to onboard flats when user don't have admin role
        {
            BulkFlatOnboardDto bulkFlatOnboardDto = new BulkFlatOnboardDto(apartmentModel.getId(), List.of(flat1, flat2));
            NivaasCustomerException exception = assertThrows(NivaasCustomerException.class, () -> onboardingRequestService.bulkAdd(bulkFlatOnboardDto));
            assertEquals(HttpStatus.FORBIDDEN, exception.getErrorCode().getHttpStatus());
            assertEquals("You Are Not Allowed To Onboard Flats", exception.getMessage());
        }
        markUserAsAdmin(apartmentModel, user.getId());
        //throws exception to onboard flats when number of flats greater than total flats of apartment
        {
            BulkFlatOnboardDto bulkFlatOnboardDto = new BulkFlatOnboardDto(apartmentModel.getId(), List.of(flat1, flat2, flat3));
            NivaasCustomerException exception = assertThrows(NivaasCustomerException.class, () -> onboardingRequestService.bulkAdd(bulkFlatOnboardDto));
            assertEquals(HttpStatus.BAD_REQUEST, exception.getErrorCode().getHttpStatus());
            assertEquals("Cannot onboard more than 2 flats", exception.getMessage());
        }
        //onboard flat1 and flat2 - owners
        {
            List<FlatBasicDTO> flatBasicDTOS = List.of(flat1, flat2);
            BulkFlatOnboardDto bulkFlatOnboardDto = new BulkFlatOnboardDto(apartmentModel.getId(), flatBasicDTOS);
            onboardingRequestService.bulkAdd(bulkFlatOnboardDto);
            CurrentApartmentDto accessDto = onboardingRequestService.findByUserAndApartmentId(loggedInUser, apartmentModel.getId());
            assertNotNull(accessDto.getCurrentApartment());
            assertEquals(accessDto.getCurrentApartment().getFlats().size(), flatBasicDTOS.size());
        }
    }

    @Test
    void tenantOnboardTest() {
        //add new apartment
        NivaasApartmentModel apartmentModel = saveTestApartment("Test Apartment");
        markUserAsAdmin(apartmentModel, user.getId());
        FlatBasicDTO flat1 = new FlatBasicDTO("101", "1234567890", "user1");
        FlatBasicDTO flat2 = new FlatBasicDTO("102", "1234567891", "user2");
        //onboard flat1 and flat2 - owners
        {
            List<FlatBasicDTO> flatBasicDTOS = List.of(flat1, flat2);
            BulkFlatOnboardDto bulkFlatOnboardDto = new BulkFlatOnboardDto(apartmentModel.getId(), flatBasicDTOS);
            onboardingRequestService.bulkAdd(bulkFlatOnboardDto);
            CurrentApartmentDto accessDto = onboardingRequestService.findByUserAndApartmentId(loggedInUser, apartmentModel.getId());
            assertNotNull(accessDto.getCurrentApartment());
            assertEquals(accessDto.getCurrentApartment().getFlats().size(), flatBasicDTOS.size());
        }
        OnboardingRequestDTO tenantOnboardRequest = new OnboardingRequestDTO();
        tenantOnboardRequest.setApartment(apartmentModel.getId());
        List<NivaasFlatModel> flatModels = flatService.getAllFlatsByApartment(apartmentModel.getId());
        assertNotNull(flatModels);
        //onboard tenant when flat not available for rent
        {
            NivaasFlatModel flatModel = flatModels.get(0);
            flatModel.setAvailableForRent(false);
            flatService.save(flatModel);
            NivaasCustomerException exception = assertThrows(NivaasCustomerException.class, () ->
                    onboardingRequestService.flatRelatedOnboarding(tenantOnboardRequest, flatModel, RelatedType.TENANT));
            assertEquals(HttpStatus.FORBIDDEN, exception.getErrorCode().getHttpStatus());
            assertEquals("Flat not available for rent", exception.getMessage());
        }
        //onboard tenant
        OnboardingRequest onboardingRequest;
        {
            NivaasFlatModel flatModel = flatModels.get(0);
            flatModel.setAvailableForRent(true);
            flatService.save(flatModel);
            onboardingRequestService.flatRelatedOnboarding(tenantOnboardRequest, flatModel, RelatedType.TENANT);
            Page<OnboardingRequest> onboardingRequests = onboardingRequestService.findByFlat(flatModel, Pageable.unpaged());
            assertEquals(onboardingRequests.getContent().size(), 1);
            onboardingRequest = onboardingRequests.getContent().get(0);
            assertEquals(onboardingRequest.getOnboardType(), OnboardType.FLAT);
            assertEquals(onboardingRequest.getRelatedUsers().size(), 1);
            assertEquals(onboardingRequest.getRelatedUsers().get(0).getRelatedType(), RelatedType.TENANT);
            assertFalse(onboardingRequest.getRelatedUsers().get(0).isRelatedUserApproved());
        }
        //duplicate onboard request should fail
        {
            NivaasCustomerException exception = assertThrows(NivaasCustomerException.class, () ->
                    onboardingRequestService.flatRelatedOnboarding(tenantOnboardRequest, flatModels.get(0), RelatedType.TENANT));
            assertEquals(HttpStatus.CONFLICT, exception.getErrorCode().getHttpStatus());
            assertEquals("Already exists", exception.getMessage());
        }
        //approve tenant
        {
            onboardingRequestService.approveFlatRelatedUsers(onboardingRequest, 2L, RelatedType.TENANT,
                    onboardingRequest.getRelatedUsers().get(0).getId());
            Page<OnboardingRequest> onboardingRequests = onboardingRequestService.findByFlat(flatModels.get(0), Pageable.unpaged());
            assertEquals(onboardingRequests.getContent().size(), 1);
            onboardingRequest = onboardingRequests.getContent().get(0);
            assertEquals(onboardingRequest.getRelatedUsers().get(0).getRelatedType(), RelatedType.TENANT);
            assertTrue(onboardingRequest.getRelatedUsers().get(0).isRelatedUserApproved());
        }
        //onboard tenant with new user and approve
        {
            setCurrentUser(2L, "9123123123");
            onboardingRequestService.flatRelatedOnboarding(tenantOnboardRequest, flatModels.get(0), RelatedType.TENANT);
            Page<OnboardingRequest> requestPage = onboardingRequestService.findByFlat(flatModels.get(0), Pageable.unpaged());
            assertEquals(requestPage.getContent().size(), 1);
            onboardingRequest = requestPage.getContent().get(0);

            onboardingRequestService.approveFlatRelatedUsers(onboardingRequest, 1L, RelatedType.TENANT,
                    onboardingRequest.getRelatedUsers().get(1).getId());
            Page<OnboardingRequest> onboardingRequests = onboardingRequestService.findByFlat(flatModels.get(0), Pageable.unpaged());
            assertEquals(onboardingRequests.getContent().size(), 1);
            assertEquals(onboardingRequests.getContent().get(0).getOnboardType(), OnboardType.FLAT);
            List<ApartmentAndFlatRelatedUsersModel> relatedUsers = onboardingRequests.getContent().get(0).getRelatedUsers();
            assertEquals(relatedUsers.size(), 2);
            List<ApartmentAndFlatRelatedUsersModel> currentRelatedUsers =
                    relatedUsers.stream().filter(related -> related.getUserId().equals(user.getId())).collect(Collectors.toList());
            assertEquals(currentRelatedUsers.size(), 1);
            assertEquals(currentRelatedUsers.get(0).getRelatedType(), RelatedType.TENANT);
            assertTrue(currentRelatedUsers.get(0).isRelatedUserApproved());
        }
    }
}
