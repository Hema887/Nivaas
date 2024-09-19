package com.juvarya.nivaas.access.mgmt.services;

import com.juvarya.nivaas.access.mgmt.model.CustomerLastLoginModel;
import com.juvarya.nivaas.access.mgmt.model.User;

public interface CustomerLastLoginService {
	CustomerLastLoginModel save(CustomerLastLoginModel customerLastLoginModel);

	CustomerLastLoginModel findByJtCustomer(User user);
}
