package com.juvarya.nivaas.access.mgmt.services.impl;

import com.juvarya.nivaas.access.mgmt.model.CustomerLastLoginModel;
import com.juvarya.nivaas.access.mgmt.model.User;
import com.juvarya.nivaas.access.mgmt.repository.CustomerLastLoginRepository;
import com.juvarya.nivaas.access.mgmt.services.CustomerLastLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CustomerLastLoginServiceImpl implements CustomerLastLoginService {

	@Autowired
	private CustomerLastLoginRepository customerLastLoginRepository;

	@Override
	@Transactional
	public CustomerLastLoginModel save(CustomerLastLoginModel customerLastLoginModel) {
		return customerLastLoginRepository.save(customerLastLoginModel);
	}

	@Override
	public CustomerLastLoginModel findByJtCustomer(User user) {
		return customerLastLoginRepository.findByCustomer(user);
	}

}
