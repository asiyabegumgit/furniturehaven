package org.perscholas.furniturehaven.repository;

import org.perscholas.furniturehaven.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE (p.name LIKE CONCAT('%', :keyword, '%') OR p.description LIKE CONCAT('%', :keyword, '%')) AND p.categoryId = :categoryId")
    List<Product> searchProducts(@Param("keyword") String keyword, @Param("categoryId") Long categoryId);
    Page<Product> findByCategoryId(Long categoryId,Pageable pageable);
}

