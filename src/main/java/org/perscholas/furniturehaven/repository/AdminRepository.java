package org.perscholas.furniturehaven.repository;

import org.perscholas.furniturehaven.model.Admin;
import org.perscholas.furniturehaven.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface AdminRepository extends JpaRepository<Admin, Long> {
    Optional<Admin> findByUsername(String username);
}
