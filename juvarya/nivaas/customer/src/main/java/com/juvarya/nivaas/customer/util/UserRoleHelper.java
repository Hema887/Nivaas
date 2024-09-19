package com.juvarya.nivaas.customer.util;

import com.juvarya.nivaas.customer.model.NivaasApartmentModel;
import com.juvarya.nivaas.customer.model.ApartmentUserRoleModel;
import com.juvarya.nivaas.customer.service.ApartmentUserRoleService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class UserRoleHelper {

    @Autowired
    private ApartmentUserRoleService apartmentUserRoleService;

    public boolean isValidApartmentAdmin(final Long userId, final NivaasApartmentModel nivaasApartmentModel) {
        ApartmentUserRoleModel apartmentUserRoleModel = apartmentUserRoleService
                .findByApartmentModelAndJtCustomer(nivaasApartmentModel, userId);
        return null != apartmentUserRoleModel;
    }
}
