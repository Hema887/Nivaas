package com.juvarya.nivaas.customer.service;

import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.customer.NivaasBaseTest;
import com.juvarya.nivaas.customer.dto.request.FlatNumbersOnboardDto;
import com.juvarya.nivaas.customer.dto.request.NivaasBasicFlatDTO;
import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

public class NivaasFlatServiceTest extends NivaasBaseTest {

    @BeforeEach
    public void init() {
        super.init();
    }

    @AfterEach
    public void cleanUp() {
        super.cleanUp();
    }

    @Test
    void bulkFlatOnboardWithOutOwnerTest() {
        //add new apartment
        NivaasApartmentModel apartmentModel = saveTestApartment("Test Apartment");
        NivaasBasicFlatDTO flat1 = new NivaasBasicFlatDTO("101");
        NivaasBasicFlatDTO flat2 = new NivaasBasicFlatDTO("102");
        NivaasBasicFlatDTO flat3 = new NivaasBasicFlatDTO("103");

        //throws exception to onboard flats when apartment not found
        {
            FlatNumbersOnboardDto onboardWithoutOwnerDto = new FlatNumbersOnboardDto(111L, List.of(flat1, flat2));
            NivaasCustomerException exception = assertThrows(NivaasCustomerException.class,
                    () -> flatService.bulkAddWithoutOwners(onboardWithoutOwnerDto));
            assertEquals(HttpStatus.NOT_FOUND, exception.getErrorCode().getHttpStatus());
            assertEquals("Apartment Not Found", exception.getMessage());
        }
        //throws exception to onboard flats when user don't have admin role
        {
            FlatNumbersOnboardDto onboardWithoutOwnerDto = new FlatNumbersOnboardDto(apartmentModel.getId(), List.of(flat1, flat2));
            NivaasCustomerException exception = assertThrows(NivaasCustomerException.class,
                    () -> flatService.bulkAddWithoutOwners(onboardWithoutOwnerDto));
            assertEquals(HttpStatus.FORBIDDEN, exception.getErrorCode().getHttpStatus());
            assertEquals("You Are Not Allowed To Onboard Flats", exception.getMessage());
        }
        markUserAsAdmin(apartmentModel, user.getId());
        //throws exception to onboard flats when number of flats greater than total flats of apartment
        {
            FlatNumbersOnboardDto onboardWithoutOwnerDto = new FlatNumbersOnboardDto(apartmentModel.getId(), List.of(flat1, flat2, flat3));
            NivaasCustomerException exception = assertThrows(NivaasCustomerException.class,
                    () -> flatService.bulkAddWithoutOwners(onboardWithoutOwnerDto));
            assertEquals(HttpStatus.BAD_REQUEST, exception.getErrorCode().getHttpStatus());
            assertEquals("Cannot onboard more than 2 flats", exception.getMessage());
        }
        //onboard flat1 and flat2 - owners
        {
            List<NivaasBasicFlatDTO> nivaasBasicFlatDTOs = List.of(flat1, flat2);
            FlatNumbersOnboardDto onboardWithoutOwnerDto = new FlatNumbersOnboardDto(apartmentModel.getId(), nivaasBasicFlatDTOs);
            flatService.bulkAddWithoutOwners(onboardWithoutOwnerDto);
            assertEquals(nivaasFlatRepository.getAllFlatsByApartment(apartmentModel.getId()).size(), onboardWithoutOwnerDto.getFlats().size());
        }
    }
}
