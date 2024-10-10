package org.perscholas.furniturehaven.service;

import org.perscholas.furniturehaven.model.Product;
import org.perscholas.furniturehaven.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    public Page<Product> getProducts(int page, int size) {
        // Create a Pageable object using PageRequest
        Pageable pageable = PageRequest.of(page, size);

        // Fetch the paginated list of products
        return productRepository.findAll(pageable);
    }
    public List<Product> findAll() {
        System.out.println("inside findAll method");
        return productRepository.findAll();
    }
    public List<Product> searchByKeyword(String keyword) {
        return productRepository.searchProducts(keyword);
    }
    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }
    public List<Product> saveProducts(List<Product> products) {
        return productRepository.saveAll(products);

    }

}