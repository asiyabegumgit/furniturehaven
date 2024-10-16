package org.perscholas.furniturehaven.service;

import jakarta.transaction.Transactional;
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
        Cart cart = findByCustomerId(customerId);
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));

        // Check if the product is already in the cart
        Optional<CartItem> existingCartItem = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (existingCartItem.isPresent()) {
            // Update quantity
            CartItem cartItem = existingCartItem.get();
            cartItem.setQuantity(cartItem.getQuantity() + quantity);
        } else {
            // Create new CartItem
            CartItem cartItem = new CartItem();
            cartItem.setProduct(product);
            cartItem.setQuantity(quantity);
            cartItem.setPrice(product.getPrice());
            cartItem.setCart(cart);
            cart.getItems().add(cartItem);
        }

        cart.calculateTotalPrice();
         cartRepository.save(cart);
        return cart;
    }



    public Cart findByCustomerId(Long customerId) {
        Customer customer = customerRepository.findById(customerId)
                .orElseThrow(() -> new IllegalArgumentException("Customer not found"));

        return cartRepository.findByCustomer(customer).orElseGet(() -> {
            Cart newCart = new Cart();
            newCart.setCustomer(customer);
            return cartRepository.save(newCart);
        });

    }

    public Cart updateItemQuantity(Long customerId, Long productId, int newQuantity) {
        Cart cart = findByCustomerId(customerId);

        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (itemToUpdate != null) {
            itemToUpdate.setQuantity(newQuantity);
            cart.calculateTotalPrice(); // Update total price
            cartRepository.save(cart);   // Save the cart, which will also update CartItem
        } else {
            throw new NoSuchElementException("Item not found in cart for product ID: " + productId);
        }

        return cart;
    }

    public Cart removeItemFromCart(Long customerId, Long productId) {
        Cart cart = findByCustomerId(customerId);

        // Find the item in the cart
        Optional<CartItem> itemToRemove = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst();

        if (itemToRemove.isPresent()) {
            // Explicitly remove the item
            CartItem item = itemToRemove.get();
            cart.getItems().remove(item);

            // You may also need to explicitly remove the item if JPA cascading isn't working properly
            // cartItemRepository.delete(item); // Assuming you have a repository for CartItem
        }

        cart.calculateTotalPrice();
        cartRepository.save(cart);
        return cart;
    }
    public void deleteCart(Long customerId)
    {
        Cart cart = findByCustomerId(customerId);
        cartRepository.delete(cart);
    }
}