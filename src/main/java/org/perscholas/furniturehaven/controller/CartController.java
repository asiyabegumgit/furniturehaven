package org.perscholas.furniturehaven.controller;


import lombok.extern.slf4j.Slf4j;
import org.perscholas.furniturehaven.model.*;
import org.perscholas.furniturehaven.service.CartService;
import org.perscholas.furniturehaven.service.CategoryService;
import org.perscholas.furniturehaven.service.CustomerService;
import org.perscholas.furniturehaven.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@Slf4j
@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private CustomerService customerService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private OrderService orderService;

    @GetMapping
    public String viewCart(Model model) {
        Customer customer = getCurrentCustomer();
        log.info("Viewing cart for customer: {}", customer.getUsername());

        Cart cart = cartService.findByCustomerId(customer.getId());
        log.debug("Retrieved cart with {} items", cart.getItems().size());

        model.addAttribute("cart", cart);
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "view-cart";
    }

    @PostMapping("/add-cart")
    public String addCart(@RequestParam Long productId, @RequestParam int quantity, Model model) {
        Customer customer = getCurrentCustomer();
        log.info("Adding product {} to cart for customer: {}, quantity: {}", productId, customer.getUsername(), quantity);

        cartService.addToCart(customer.getId(), productId, quantity);
        log.debug("Product added to cart successfully");

        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "redirect:/cart";
    }

    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId, Model model) {
        Customer customer = getCurrentCustomer();
        log.info("Removing product {} from cart for customer: {}", productId, customer.getUsername());

        cartService.removeItemFromCart(customer.getId(), productId);
        log.debug("Product removed from cart successfully");

        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "redirect:/cart";
    }

    @PostMapping("/update/{productId}")
    public String updateItemQuantity(@PathVariable Long productId, @RequestParam String action, Model model) {
        Customer customer = getCurrentCustomer();
        log.info("Updating quantity for product {} in cart for customer: {}, action: {}", productId, customer.getUsername(), action);

        Cart cart = cartService.findByCustomerId(customer.getId());
        CartItem itemToUpdate = cart.getItems().stream()
                .filter(item -> item.getProduct().getId().equals(productId))
                .findFirst()
                .orElse(null);

        if (itemToUpdate != null) {
            int newQuantity = itemToUpdate.getQuantity();
           
            if ("increase".equals(action)) {
               
                newQuantity++;
            } else if ("decrease".equals(action)) {
                newQuantity--;
            }

            cartService.updateItemQuantity(customer.getId(), productId, newQuantity);
            log.debug("Cart item quantity updated to: {}", newQuantity);
        } else {
            log.warn("Attempted to update non-existent cart item: {}", productId);
        }

        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "redirect:/cart";
    }

    @PostMapping("/checkout")
    public String checkout(Model model, Authentication authentication) {
        String username = authentication.getName();
        log.info("Initiating checkout for customer: {}", username);

        try {
            Customer customer = customerService.findByUsername(username)
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            Cart cart = cartService.findByCustomerId(customer.getId());

            model.addAttribute("customer", customer);
            model.addAttribute("cart", cart);

            List<Category> categories = categoryService.getAllCategories();
            model.addAttribute("categories", categories);

            log.debug("Checkout prepared for customer: {}, total items: {}", username, cart.getItems().size());
            return "checkout";
        } catch (RuntimeException e) {
            log.error("Error during checkout for customer: {}", username, e);
            return "error";
        }
    }

    @PostMapping("/receipt")
    public String showReceiptPage(Authentication authentication, Model model,@RequestParam("name") String name,
    @RequestParam("address") String address,@RequestParam("city") String city,@RequestParam("state") String state,
    @RequestParam("zip") String zip,@RequestParam("billingAmount") double amount) {
        Customer customer = getCurrentCustomer();
        log.info("Generating receipt for customer: {}, amount: {}", customer.getUsername(), amount);

        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        Order order = new Order();
        order.setCustomer(customer);
        order.setName(name);
        order.setAddress(address);
        order.setCity(city);
        order.setState(state);
        order.setZip(zip);
        order.setOrderDate(LocalDateTime.now());
        order.setTotalAmount(amount);
        order.setOrderNumber(generateOrderNumber());

        try {
            Order savedOrder = orderService.saveOrder(order);
            if (savedOrder != null) {
                cartService.deleteCart(customer.getId());
                model.addAttribute("order", savedOrder);
                log.info("Order created successfully: {}", savedOrder.getOrderNumber());
                return "receipt";
            }
        } catch (Exception e) {
            log.error("Error creating order for customer: {}", customer.getUsername(), e);
        }

        return "redirect:/error";
    }

    private String generateOrderNumber() {
        long timestamp = System.currentTimeMillis();
        return String.valueOf(timestamp);
    }

    public Customer getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() != null) {
            String userName = ((UserDetails) authentication.getPrincipal()).getUsername();
            try {
                return customerService.findByUsername(userName)
                        .orElseThrow(() -> new RuntimeException("Customer not found"));
            } catch (RuntimeException e) {
                log.error("Error retrieving current customer", e);
                return null;
            }
        }
        log.warn("No authenticated customer found");
        return null;
    }
}
