package org.perscholas.furniturehaven.service;

import lombok.extern.slf4j.Slf4j;
import org.perscholas.furniturehaven.model.Category;
import org.perscholas.furniturehaven.model.Product;
import org.perscholas.furniturehaven.repository.CategoryRepository;
import org.perscholas.furniturehaven.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CategoryRepository categoryRepository;

    public Page<Product> searchByKeyword(String keyword, Long categoryId, Pageable pageable) {
        log.info("Searching products with keyword: '{}', categoryId: {}", keyword, categoryId);
        Page<Product> results = productRepository.findByKeywordAndCategory(keyword, categoryId, pageable);
        log.debug("Found {} products matching the search criteria", results.getTotalElements());
        return results;
    }

    public Optional<Product> getProductById(Long id) {
        log.info("Fetching product with id: {}", id);
        Optional<Product> product = productRepository.findById(id);
        if (product.isPresent()) {
            log.debug("Product found: {}", product.get());
        } else {
            log.warn("Product not found with id: {}", id);
        }
        return product;
    }

    public List<Product> getAllProductsList() {
        log.info("Fetching all products");
        List<Product> products = productRepository.findAll();
        log.debug("Retrieved {} products", products.size());
        return products;
    }

    public Page<Product> getAllProducts(int page, int size) {
        log.info("Fetching paginated products, page: {}, size: {}", page, size);
        Pageable pageable = PageRequest.of(page, size);
        Page<Product> productPage = productRepository.findAll(pageable);
        log.debug("Retrieved {} products for page {}", productPage.getNumberOfElements(), page);
        return productPage;
    }

    public Page<Product> getProductsByCategory(long categoryId, Pageable pageable) {
        log.info("Fetching products for category: {}", categoryId);
        Page<Product> products = productRepository.findByCategoryId(categoryId, pageable);
        log.debug("Found {} products in category {}", products.getTotalElements(), categoryId);
        return products;
    }

    public void deleteProduct(Long id) {
        log.info("Deleting product with id: {}", id);
        try {
            productRepository.deleteById(id);
            log.debug("Product deleted successfully");
        } catch (Exception e) {
            log.error("Error deleting product with id: {}", id, e);
            throw e;
        }
    }

    public Product updateProduct(Long id, Product updatedProduct) {
        log.info("Updating product with id: {}", id);
        try {
            Product savedProduct = productRepository.findById(id)
                    .map(product -> {
                        product.setName(updatedProduct.getName());
                        product.setDescription(updatedProduct.getDescription());
                        product.setPrice(updatedProduct.getPrice());
                        product.setCategory(updatedProduct.getCategory());
                        product.setCategoryId(updatedProduct.getCategoryId());
                        product.setBrand(updatedProduct.getBrand());
                        product.setImage(updatedProduct.getImage());
                        product.setRating(updatedProduct.getRating());
                        log.debug("Product details updated: {}", product);
                        return productRepository.save(product);
                    })
                    .orElseThrow(() -> {
                        log.warn("Attempted to update non-existent product with id: {}", id);
                        return new RuntimeException("Product not found");
                    });
            log.info("Product updated successfully: {}", savedProduct.getId());
            return savedProduct;
        } catch (Exception e) {
            log.error("Error updating product with id: {}", id, e);
            throw e;
        }
    }

    public List<Product> saveProducts(List<Product> products) {
        log.info("Saving batch of {} products", products.size());
        try {
            for (Product product : products) {
                if (product.getCategoryId() != null) {
                    log.debug("Fetching category for product: {}", product.getName());
                    Category category = categoryRepository.findById(product.getCategoryId())
                            .orElseThrow(() -> {
                                log.warn("Category not found for ID: {}", product.getCategoryId());
                                return new RuntimeException("Category not found for ID: " + product.getCategoryId());
                            });
                    product.setCategory(category);
                }
            }
            List<Product> savedProducts = productRepository.saveAll(products);
            log.info("Successfully saved {} products", savedProducts.size());
            return savedProducts;
        } catch (Exception e) {
            log.error("Error saving batch of products", e);
            throw e;
        }
    }
}
