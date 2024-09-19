package com.juvarya.nivaas.access.mgmt.services.impl;

import com.juvarya.nivaas.access.mgmt.dto.request.UserUpdateDto;
import com.juvarya.nivaas.access.mgmt.model.User;
import com.juvarya.nivaas.access.mgmt.repository.RoleRepository;
import com.juvarya.nivaas.access.mgmt.repository.UserRepository;
import com.juvarya.nivaas.access.mgmt.services.UserService;
import com.juvarya.nivaas.auth.UserServiceAdapter;
import com.juvarya.nivaas.auth.exception.handling.ErrorCode;
import com.juvarya.nivaas.auth.exception.handling.NivaasCustomerException;
import com.juvarya.nivaas.commonservice.dto.UserDTO;
import com.juvarya.nivaas.commonservice.enums.ERole;
import com.juvarya.nivaas.access.mgmt.model.Role;
import com.juvarya.nivaas.commonservice.user.UserDetailsImpl;
import com.juvarya.nivaas.utils.SecurityUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

@Service
@Slf4j
public class UserServiceImpl implements UserService, UserServiceAdapter {

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private RoleRepository roleRepository;

	@Override
	public User saveUser(User user) {
		return userRepository.save(user);
	}

	@Override
	public void updateUser(final UserUpdateDto details, final Long userId) {
		UserDetailsImpl loggedInUser = SecurityUtils.getCurrentUserDetails();

		User user = findById(loggedInUser.getId());
		if (user == null || !Objects.equals(userId, user.getId())) {
			log.error("User Not Found With Given Id" + " " + loggedInUser.getId() + "or invalid update");
			throw new NivaasCustomerException(ErrorCode.USER_NOT_FOUND);
		}
		log.info("Updating details for user with ID: {}", loggedInUser.getId());
		if (!existsByEmail(details.getEmail(), userId)) {
			user.setEmail(details.getEmail());
		} else {
			log.warn("Email '{}' is already in use", details.getEmail());
			throw new NivaasCustomerException(ErrorCode.EMAIL_ALREADY_IN_USE);
		}
		user.setFullName(details.getFullName());
		saveUser(user);
	}

	@Override
	public Long onBoardUser(final String fullName, final String mobileNumber, final Set<ERole> userRoles) {
		 log.info("Onboarding user with mobile number {}", mobileNumber);
		Optional<User> user = findByPrimaryContact(mobileNumber);
		if (user.isEmpty()) {
			User newUser = new User();
			newUser.setPrimaryContact(mobileNumber);
			newUser.setRoles(roleRepository.findByNameIn(userRoles));
			newUser.setCreationTime(new Date());
			newUser.setFullName(fullName);
			return userRepository.save(newUser).getId();
		}
		return user.get().getId();
	}

	@Override
	public void addUserRole(final Long userId, final ERole userRole) {
		log.info("Adding role {} for user {}", userRole, userId);
		User user = findById(userId);
		if (null != user) {
			Set<Role> userRoles = user.getRoles();
			Optional<Role> newRole = roleRepository.findByName(userRole);
			if (null != userRoles && newRole.isPresent() && !userRoles.contains(newRole.get())) {
				userRoles.add(newRole.get());
				user.setVersion(user.getVersion()+1);
			}
			user.setRoles(userRoles);
			userRepository.save(user);
		}
	}

	@Override
	public void removeUserRole(final String mobileNumber, final ERole userRole) {
		 log.info("Removing role {} for user with mobile number {}", userRole, mobileNumber);
		Optional<User> user = findByPrimaryContact(mobileNumber);
		if (user.isPresent()) {
			User updateRoleForUser = user.get();
			Set<Role> userRoles = updateRoleForUser.getRoles();
			Optional<Role> deleteRole = roleRepository.findByName(userRole);
			if (null != userRoles && deleteRole.isPresent() && userRoles.contains(deleteRole.get())) {
				userRoles.remove(deleteRole.get());
				updateRoleForUser.setVersion(updateRoleForUser.getVersion()+1);
			}
			updateRoleForUser.setRoles(userRoles);
			userRepository.save(updateRoleForUser);
		}
	}

	@Override
	@Transactional
	public UserDTO getUserById(Long id) {
		Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.map(UserServiceImpl::convertToUserDto).orElse(null);
    }

	@Override
	public User findById(Long id) {
		Optional<User> optionalUser = userRepository.findById(id);
        return optionalUser.orElse(null);
    }

	@Override
	public List<User> findByIds(List<Long> ids) {
		List<User> optionalUser = userRepository.findAllById(ids);
		return optionalUser;
	}

	@Override
	public User findByEmail(String email) {
		Optional<User> optionalUser = userRepository.findByEmail(email);
        return optionalUser.orElse(null);
    }

	@Override
	public Page<User> findByRolesContainsIgnoreCase(String role, Pageable pageable) {
		Optional<Role> optionalRole = roleRepository.findByName(ERole.valueOf(role));
		return userRepository.findByRolesContainsIgnoreCase(optionalRole.get(), pageable);
	}

	@Override
	public Optional<User> findByPrimaryContact(String primaryContact) {
		try {
			return userRepository.findByPrimaryContact(primaryContact);
		} catch (Exception e) {
			return Optional.empty();
		}

	}

	@Override
	public Page<User> getAllUsers(Pageable pageable) {
		return userRepository.findAll(pageable);
	}

	@Override
	public Page<User> findByPostalCodes(List<Long> code, String type, Pageable pageable) {
		return userRepository.findByPostalCodesAndType(code, type, pageable);
	}

	@Override
	public Optional<User> findByCustomerAndType(Long customerId, String type) {
		return userRepository.findByIdAndType(customerId, type);
	}

	public Boolean existsByEmail(final String email, final Long userId) {
		return userRepository.existsByEmailAndNotUserId(email, userId);
	}

	private static UserDTO convertToUserDto(User user) {
		Set<com.juvarya.nivaas.commonservice.dto.Role> userRoles = new HashSet<>();
		user.getRoles().forEach(role -> userRoles.add(new com.juvarya.nivaas.commonservice.dto.Role(role.getId(), role.getName())));
		return UserDTO.builder()
				.id(user.getId())
				.fullName(user.getFullName())
				.primaryContact(user.getPrimaryContact())
				.email(user.getEmail())
				.creationTime(user.getCreationTime())
				.token(user.getFcmToken())
				.username(user.getUsername())
				.gender(user.getGender())
				.profilePicture(user.getProfilePicture())
				.type(user.getType())
				.roles(userRoles)
				.version(user.getVersion())
				.build();
	}
}
