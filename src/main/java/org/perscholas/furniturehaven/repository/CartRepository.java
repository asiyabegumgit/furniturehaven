package org.perscholas.furniturehaven.repository;

import org.perscholas.furniturehaven.model.Cart;
import org.perscholas.furniturehaven.model.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart,Long> {
    Cart findByCustomer(Customer customer);
}
