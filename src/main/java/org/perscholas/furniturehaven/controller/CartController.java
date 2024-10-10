package org.perscholas.furniturehaven.controller;


import org.perscholas.furniturehaven.model.Cart;
import org.perscholas.furniturehaven.model.CartItem;
import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.service.CartService;
import org.perscholas.furniturehaven.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private CustomerService customerService;
    @GetMapping
    public String viewCart(Model model) {
        Customer customer = getCurrentCustomer();

        Cart cart=cartService.findByCustomerId(customer.getId());


        model.addAttribute("cart", cart);

        return "view-cart";
    }
    @PostMapping("/add-cart")
    public String addCart(@RequestParam Long productId, @RequestParam int quantity, Model model) {
       Customer customer = getCurrentCustomer();
        cartService.addToCart(customer.getId(), productId, quantity);


        return "redirect:/cart";
    }


    @PostMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId) {
        Customer customer = getCurrentCustomer();
        cartService.removeItemFromCart(customer.getId(), productId);
        return "redirect:/cart";
    }

//    @PostMapping("/update/{productId}")
//    public String updateCartItemQuantity(@PathVariable Long productId,@RequestParam int quantity,@Req) {
//        Customer customer = getCurrentCustomer();
//        cartService.updateItemQuantity(customer.getId(), productId,quantity);
//        return "redirect:/cart";
//    }
@PostMapping("/update/{productId}")
public String updateItemQuantity(@PathVariable Long productId,

                                            @RequestParam String action) {
        Customer customer = getCurrentCustomer();
    Cart cart = cartService.findByCustomerId(customer.getId());


    CartItem itemToUpdate = cart.getItems().stream()
            .filter(item -> item.getProduct().getId().equals(productId))
            .findFirst()
            .orElse(null);

    if (itemToUpdate != null) {
        int newQuantity = itemToUpdate.getQuantity();

        if ("increase".equals(action)) {
            newQuantity++;  // Increase quantity by 1
        } else if ("decrease".equals(action)) {

                newQuantity--;  // Decrease quantity by 1, but not below 1

        }

        // Update quantity in service
        cartService.updateItemQuantity(customer.getId(), productId, newQuantity);

    }
    return "redirect:/cart";
}

    @PostMapping("/checkout")
    public String checkout(Model model, Authentication authentication) {
        // Get the authenticated user's username
        String username = authentication.getName();

        // Find the shopper using their username
        Customer customer = customerService.findByUsername(username).orElseThrow(() -> new RuntimeException("Customer not found"));

        // Retrieve the shopper's cart
        Cart cart = cartService.findByCustomerId(customer.getId());

        // Prepare the receipt model attributes
        model.addAttribute("customer", customer);
        model.addAttribute("cart", cart);


        return "checkout"; // A Thymeleaf template that displays the receipt
    }

    public Customer getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();



        if (authentication != null && authentication.getPrincipal() != null) {
            String userName = ((UserDetails) authentication.getPrincipal()).getUsername();
            return customerService.findByUsername(userName).orElseThrow(() -> new RuntimeException("Customer not found"));
        }
        return null;
    }
}
