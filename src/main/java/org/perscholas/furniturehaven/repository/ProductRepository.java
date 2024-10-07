package org.perscholas.furniturehaven.repository;

import org.perscholas.furniturehaven.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

}
