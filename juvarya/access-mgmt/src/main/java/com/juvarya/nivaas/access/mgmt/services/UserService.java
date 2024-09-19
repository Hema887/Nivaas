/**
 * 
 */
package com.juvarya.nivaas.access.mgmt.services;

import com.juvarya.nivaas.access.mgmt.dto.request.UserUpdateDto;
import com.juvarya.nivaas.access.mgmt.model.User;
import com.juvarya.nivaas.commonservice.enums.ERole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * @author juvi
 *
 */
public interface UserService {

	User saveUser(User user);

	void updateUser(final UserUpdateDto details, final Long userId);

	Long onBoardUser(final String fullName, final String mobileNumber, final Set<ERole> userRoles);

	void addUserRole(final Long userId, final ERole userRole);

	void removeUserRole(final String mobileNumber, final ERole userRole);

	User findById(Long id);

	List<User> findByIds(List<Long> ids);

	User findByEmail(String email);

	Page<User> findByRolesContainsIgnoreCase(String role, Pageable pageable);

	Optional<User> findByPrimaryContact(String primaryContact);

	Page<User> getAllUsers(Pageable pageable);

	Page<User> findByPostalCodes(@Param("code") List<Long> code, String type, Pageable pageable);

	Optional<User> findByCustomerAndType(Long customerId, String type);

	Boolean existsByEmail(final String email, final Long userId);
}
