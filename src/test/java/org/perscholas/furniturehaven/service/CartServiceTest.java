package org.perscholas.furniturehaven.service;

import static org.mockito.Mockito.*;
import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.perscholas.furniturehaven.model.Cart;
import org.perscholas.furniturehaven.model.CartItem;
import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.model.Product;
import org.perscholas.furniturehaven.repository.CartItemRepository;
import org.perscholas.furniturehaven.repository.CartRepository;
import org.perscholas.furniturehaven.repository.CustomerRepository;
import org.perscholas.furniturehaven.repository.ProductRepository;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.List;

@ExtendWith(MockitoExtension.class)
class CartServiceTest {

    @InjectMocks
    private CartService cartService;

    @Mock
    private CartRepository cartRepository;

    @Mock
    private CustomerRepository customerRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private CartItemRepository cartItemRepository;

    private Customer customer;
    private Product product;
    private Cart cart;

    @BeforeEach
    void setUp() {
        customer = new Customer();
        customer.setId(1L);
        customer.setUsername("testUser");

        product = new Product();
        product.setId(1L);
        product.setPrice(100.0);

        cart = new Cart();
        cart.setCustomer(customer);
        cart.setItems(new ArrayList<>());
    }

    @Test
    void testAddToCart_NewItem() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartRepository.findByCustomer(customer)).thenReturn(Optional.empty());
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart updatedCart = cartService.addToCart(customer.getId(), product.getId(), 1);

        assertEquals(1, updatedCart.getItems().size());
        assertEquals(100.0, updatedCart.getSubtotal());
    }

    @Test
    void testAddToCart_ExistingItem() {
        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(1);
        cart.getItems().add(existingItem);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(productRepository.findById(product.getId())).thenReturn(Optional.of(product));
        when(cartRepository.findByCustomer(customer)).thenReturn(Optional.of(cart));
        when(cartRepository.save(any(Cart.class))).thenReturn(cart);

        Cart updatedCart = cartService.addToCart(customer.getId(), product.getId(), 1);

        assertEquals(1, updatedCart.getItems().size());
        assertEquals(2, updatedCart.getItems().get(0).getQuantity());
        assertEquals(100.0, updatedCart.getSubtotal());
    }

    @Test
    void testUpdateItemQuantity_ItemExists() {
        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        existingItem.setQuantity(1);
        cart.getItems().add(existingItem);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomer(customer)).thenReturn(Optional.of(cart));

        Cart updatedCart = cartService.updateItemQuantity(customer.getId(), product.getId(), 3);

        assertEquals(3, updatedCart.getItems().get(0).getQuantity());
    }

    @Test
    void testUpdateItemQuantity_ItemDoesNotExist() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomer(customer)).thenReturn(Optional.of(cart));

        assertThrows(NoSuchElementException.class, () -> {
            cartService.updateItemQuantity(customer.getId(), product.getId(), 1);
        });
    }

    @Test
    void testRemoveItemFromCart_ItemExists() {
        CartItem existingItem = new CartItem();
        existingItem.setProduct(product);
        cart.getItems().add(existingItem);

        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomer(customer)).thenReturn(Optional.of(cart));

        Cart updatedCart = cartService.removeItemFromCart(customer.getId(), product.getId());

        assertTrue(updatedCart.getItems().isEmpty());
    }

    @Test
    void testRemoveItemFromCart_ItemDoesNotExist() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomer(customer)).thenReturn(Optional.of(cart));

        Cart updatedCart = cartService.removeItemFromCart(customer.getId(), product.getId());

        assertEquals(0, updatedCart.getItems().size());
    }

    @Test
    void testDeleteCart() {
        when(customerRepository.findById(customer.getId())).thenReturn(Optional.of(customer));
        when(cartRepository.findByCustomer(customer)).thenReturn(Optional.of(cart));

        cartService.deleteCart(customer.getId());

        verify(cartRepository).delete(cart);
    }
}