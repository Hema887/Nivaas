package com.juvarya.nivaas.access.mgmt.services.impl;

import com.juvarya.nivaas.access.mgmt.model.User;
import com.juvarya.nivaas.access.mgmt.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl {
	@Autowired
	UserRepository userRepository;

	@Transactional
	public User loadUserByPrimaryContact(String primaryContact) {
		try {
			Optional<User> optionalUser = userRepository.findByPrimaryContact(primaryContact);
			return optionalUser.orElse(null);
		} catch (Exception e) {
			return null;
		}
	}
	
	

}
