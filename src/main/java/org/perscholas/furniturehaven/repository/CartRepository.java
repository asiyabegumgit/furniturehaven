package org.perscholas.furniturehaven.repository;

import org.perscholas.furniturehaven.model.Cart;
import org.perscholas.furniturehaven.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByCustomer(Customer customer);

}
