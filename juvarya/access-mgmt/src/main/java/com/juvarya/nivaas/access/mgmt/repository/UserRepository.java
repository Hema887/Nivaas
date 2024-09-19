package com.juvarya.nivaas.access.mgmt.repository;

import com.juvarya.nivaas.access.mgmt.model.Role;
import com.juvarya.nivaas.access.mgmt.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	Optional<User> findByUsername(String username);

	Boolean existsByUsername(String username);

	@Query("SELECT COUNT(u) > 0 FROM User u WHERE u.email = :email AND u.id <> :userId")
	Boolean existsByEmailAndNotUserId(@Param("email") String email, @Param("userId") Long userId);

	Optional<User> findByEmail(String email);

	Page<User> findByRolesContainsIgnoreCase(Role role, Pageable pageable);

	Boolean existsByPrimaryContact(String primaryContact);

	Optional<User> findByPrimaryContact(String primaryContact);

	Optional<User> findByIdAndType(Long userId, String type);

	@Query("SELECT users FROM User users WHERE users.postalCode IN :code AND users.type =:type")
	Page<User> findByPostalCodesAndType(@Param("code") List<Long> code, @Param("type") String type, Pageable pageable);
}
