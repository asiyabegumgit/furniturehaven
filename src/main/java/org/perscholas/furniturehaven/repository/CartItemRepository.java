package org.perscholas.furniturehaven.repository;

import org.perscholas.furniturehaven.model.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartItemRepository extends JpaRepository<CartItem,Long> {
}
