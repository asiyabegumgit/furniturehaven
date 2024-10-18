package org.perscholas.furniturehaven.repository;

import org.perscholas.furniturehaven.model.Order;
import org.springframework.data.jpa.repository.JpaRepository;

public interface OrderRepository extends JpaRepository<Order, Long> {
}
