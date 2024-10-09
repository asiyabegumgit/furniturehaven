package org.perscholas.furniturehaven.service;

import jakarta.transaction.Transactional;
import org.perscholas.furniturehaven.model.Cart;
import org.perscholas.furniturehaven.model.CartItem;
import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.model.Product;
import org.perscholas.furniturehaven.repository.CartRepository;
import org.perscholas.furniturehaven.repository.CustomerRepository;
import org.perscholas.furniturehaven.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class CartService {
    @Autowired
    private CartRepository cartRepository;
    @Autowired
    private CustomerRepository customerRepository;
    @Autowired
    private ProductRepository productRepository;

    @Transactional
    public Cart addToCart(Long customerId, Long productId, int quantity) {
        System.out.println("CustomerId***->" + customerId);
        Optional<Cart> optionalCart= cartRepository.findByCustomerId(customerId);
        Cart cart = optionalCart.orElseGet(Cart::new);
        Optional<Product> product = productRepository.findById(productId);
        CartItem existingItems = null;
        if (product.isPresent()) {
            existingItems = cart.getItems().stream().filter(item -> item.getProduct().getId().equals(productId)).findFirst().orElse(null);
        }
        if (existingItems != null) {
            existingItems.setQuantity(existingItems.getQuantity() + quantity);
        } else {
            CartItem newCartItem = new CartItem();
            newCartItem.setCart(cart);
            newCartItem.setQuantity(quantity);
            newCartItem.setProduct(product.get());
            newCartItem.setPrice(product.get().getPrice());
            cart.getItems().add(newCartItem);
        }
        cart.setCustomer(customerRepository.findById(customerId).get());
        System.out.println("success");
        updateCartTotals(cart);

        return cartRepository.save(cart);

    }

    public Cart getCart(Long id) {
        return cartRepository.findById(id).get();
    }

    public Cart findByCustomerId(Long customerId) {
       return cartRepository.findByCustomerId(customerId).get();

    }
    private void updateCartTotals(Cart cart) {
        cart.calculateTotalPrice();
    }
    public Cart updateItemQuantity(Long customerId, Long productId, int newQuantity) {
        Cart cart = getCart(customerId);

        cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .ifPresent(item -> item.setQuantity(newQuantity));

        updateCartTotals(cart);
        return cartRepository.save(cart);
    }
    public Cart removeItemFromCart(Long customerId, Long productId) {
        Cart cart = getCart(customerId);

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

        updateCartTotals(cart);
        return cartRepository.save(cart);
    }
}