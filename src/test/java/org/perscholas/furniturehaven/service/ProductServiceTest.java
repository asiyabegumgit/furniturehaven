package org.perscholas.furniturehaven.service;
import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.perscholas.furniturehaven.model.Category;
import org.perscholas.furniturehaven.model.Product;
import org.perscholas.furniturehaven.repository.CategoryRepository;
import org.perscholas.furniturehaven.repository.ProductRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @InjectMocks
    private ProductService productService;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CategoryRepository categoryRepository;

    private Product product;
    private Category category;

    @BeforeEach
    void setUp() {
        category = new Category();
        category.setCategoryId(1L);
        category.setCategoryName("Livingrooms");

        product = new Product();
        product.setId(1L);
        product.setName("Sectional Sofa");
        product.setDescription("A spacious sectional sofa with adjustable chaise lounge.");
        product.setPrice(1299.99);
        product.setCategory(category);
        product.setCategoryId(category.getCategoryId());
    }

    @Test
    void testSearchByKeyword() {
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepository.findByKeywordAndCategory("Sectional Sofa", 1L, PageRequest.of(0, 10))).thenReturn(productPage);

        Page<Product> result = productService.searchByKeyword("Sectional Sofa", 1L, PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Sectional Sofa", result.getContent().get(0).getName());
    }

    @Test
    void testGetProductById_ProductExists() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));

        Optional<Product> result = productService.getProductById(product.getId());

        assertTrue(result.isPresent());
        assertEquals("Sectional Sofa", result.get().getName());
    }

    @Test
    void testGetProductById_ProductNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        Optional<Product> result = productService.getProductById(product.getId());

        assertFalse(result.isPresent());
    }

    @Test
    void testGetAllProductsList() {
        List<Product> productList = new ArrayList<>();
        productList.add(product);

        when(productRepository.findAll()).thenReturn(productList);

        List<Product> result = productService.getAllProductsList();

        assertEquals(1, result.size());
        assertEquals("Sectional Sofa", result.get(0).getName());
    }

    @Test
    void testGetAllProducts() {
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepository.findAll(PageRequest.of(0, 10))).thenReturn(productPage);

        Page<Product> result = productService.getAllProducts(0, 10);

        assertEquals(1, result.getTotalElements());
        assertEquals("Sectional Sofa", result.getContent().get(0).getName());
    }

    @Test
    void testGetProductsByCategory() {
        List<Product> productList = new ArrayList<>();
        productList.add(product);
        Page<Product> productPage = new PageImpl<>(productList);

        when(productRepository.findByCategoryId(category.getCategoryId(), PageRequest.of(0, 10))).thenReturn(productPage);

        Page<Product> result = productService.getProductsByCategory(category.getCategoryId(), PageRequest.of(0, 10));

        assertEquals(1, result.getTotalElements());
        assertEquals("Sectional Sofa", result.getContent().get(0).getName());
    }

    @Test
    void testDeleteProduct() {
        doNothing().when(productRepository).deleteById(product.getId());

        assertDoesNotThrow(() -> productService.deleteProduct(product.getId()));
        verify(productRepository, times(1)).deleteById(product.getId());
    }

    @Test
    void testUpdateProduct_ProductExists() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        Product updatedProduct = new Product();
        updatedProduct.setName("Sectional Sofa");
        updatedProduct.setDescription("A spacious sectional sofa with adjustable chaise lounge.");
        updatedProduct.setPrice(1399.99);
        updatedProduct.setCategoryId(category.getCategoryId());

        when(productRepository.save(any(Product.class))).thenReturn(product);

        Product result = productService.updateProduct(product.getId(), updatedProduct);

        assertEquals("Sectional Sofa", result.getName());
        assertEquals(1399.99, result.getPrice());
    }

    @Test
    void testUpdateProduct_ProductNotFound() {
        when(productRepository.findById(product.getId())).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> productService.updateProduct(product.getId(), product));
    }

    @Test
    void testSaveProducts() {
        List<Product> products = new ArrayList<>();
        products.add(product);

        when(categoryRepository.findById(category.getCategoryId())).thenReturn(Optional.of(category));
        when(productRepository.saveAll(products)).thenReturn(products);

        List<Product> savedProducts = productService.saveProducts(products);

        assertEquals(1, savedProducts.size());
        assertEquals("Sectional Sofa", savedProducts.get(0).getName());
    }
}