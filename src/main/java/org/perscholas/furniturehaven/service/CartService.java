package org.perscholas.furniturehaven.service;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.perscholas.furniturehaven.model.Cart;
import org.perscholas.furniturehaven.model.CartItem;
import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.model.Product;
import org.perscholas.furniturehaven.repository.CartItemRepository;
import org.perscholas.furniturehaven.repository.CartRepository;
import org.perscholas.furniturehaven.repository.CustomerRepository;
import org.perscholas.furniturehaven.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
@Slf4j
@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private CartItemRepository cartItemRepository;

    @Transactional
    public Cart addToCart(Long customerId, Long productId, int quantity) {
        log.info("Adding product with ID {} to cart for customer ID {}", productId, customerId);

        Cart cart = findByCustomerId(customerId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> {
                    log.error("Product not found with ID {}", productId);
                    return new RuntimeException("Product not found");
                });

        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            CartItem cartItem = existingCartItem.get();
            log.info("Updating quantity for product ID {} in cart", productId);
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice());
            cartItem.setCart(cart);
            cart.getItems().add(cartItem);
            log.info("Added new item to cart: {}", cartItem);
        }

        cart.calculateTotalPrice();
        cartRepository.save(cart);
        log.info("Cart updated for customer ID {}. Total price: {}", customerId, cart.getSubtotal());
        return cart;
    }

    public Cart findByCustomerId(Long customerId) {
        log.info("Finding cart for customer ID {}", customerId);
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> {
                    log.error("Customer not found with ID {}", customerId);
                    return new IllegalArgumentException("Customer not found");
                });

        return cartRepository.findByCustomer(customer).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setCustomer(customer);
            log.info("Creating new cart for customer ID {}", customerId);
            return cartRepository.save(newCart);
        });
    }

    public Cart updateItemQuantity(Long customerId, Long productId, int newQuantity) {
        log.info("Updating quantity for product ID {} in cart for customer ID {}", productId, customerId);
        Cart cart = findByCustomerId(customerId);

        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (itemToUpdate != null) {
            itemToUpdate.setQuantity(newQuantity);
            cart.calculateTotalPrice();
            cartRepository.save(cart);
            log.info("Updated quantity for product ID {} to {} in cart", productId, newQuantity);
        } else {
            log.error("Item not found in cart for product ID: {}", productId);
            throw new NoSuchElementException("Item not found in cart for product ID: " + productId);
        }

        return cart;
    }

    public Cart removeItemFromCart(Long customerId, Long productId) {
        log.info("Removing product ID {} from cart for customer ID {}", productId, customerId);
        Cart cart = findByCustomerId(customerId);

        Optional<CartItem> itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (itemToRemove.isPresent()) {
            CartItem item = itemToRemove.get();
            cart.getItems().remove(item);
            log.info("Removed item from cart: {}", item);
        } else {
            log.warn("Product ID {} not found in cart for customer ID {}", productId, customerId);
        }

        cart.calculateTotalPrice();
        cartRepository.save(cart);
        return cart;
    }

    public void deleteCart(Long customerId) {
        log.info("Deleting cart for customer ID {}", customerId);
        Cart cart = findByCustomerId(customerId);
        cart.getItems().clear();

        // You may also need to explicitly remove the item if JPA cascading isn't working properly
        // cartItemRepository.delete(item); // Assuming you have a repository for CartItem
        cartRepository.delete(cart);
        log.info("Cart deleted for customer ID {}", customerId);
    }
}