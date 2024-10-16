package org.perscholas.furniturehaven.service;

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

@Service
public class ProductService {
    @Autowired
    private ProductRepository productRepository;
   @Autowired
   private CategoryRepository categoryRepository;

    public List<Product> searchByKeyword(String keyword,Long categoryId) {
        return productRepository.searchProducts(keyword,categoryId);
    }
    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }
    public List<Product> getAllProductsList() {


        // Fetch the list of products
        return productRepository.findAll();
    }
    public Page<Product> getAllProducts(int page, int size) {
        // Create a Pageable object using PageRequest
        Pageable pageable = PageRequest.of(page, size);

        // Fetch the paginated list of products
        return productRepository.findAll(pageable);
    }
    public Page<Product> getProductsByCategory(long categoryId,Pageable pageable) {
        return productRepository.findByCategoryId(categoryId,pageable);


    }
    public void deleteProduct(Long id)
    {
        productRepository.deleteById(id);
    }
    public Product updateProduct( Long id,Product updatedProduct)
    {
        return productRepository.findById(id).map(product-> {
                    product.setName(updatedProduct.getName());
                    product.setDescription(updatedProduct.getDescription());
                    product.setPrice(updatedProduct.getPrice());
                    product.setCategory(updatedProduct.getCategory());
                    product.setCategoryId(updatedProduct.getCategoryId());
                    product.setBrand(updatedProduct.getBrand());
                    product.setImage(updatedProduct.getImage());
                    product.setRating(updatedProduct.getRating());
                    return productRepository.save(product);
                    })
                .orElseThrow(()-> new RuntimeException("Product not found"));
    }
    public List<Product> saveProducts(List<Product> products) {
        for (Product product : products) {
            // Check if the categoryId is set and fetch the corresponding Category
            if (product.getCategoryId() != null) {
                Category category = categoryRepository.findById(product.getCategoryId())
                        .orElseThrow(() -> new RuntimeException("Category not found for ID: " + product.getCategoryId()));
                product.setCategory(category);  // Set the Category object
            }
        }
            return productRepository.saveAll(products);


    }

}