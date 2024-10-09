package org.perscholas.furniturehaven.controller;


import org.perscholas.furniturehaven.model.Cart;
import org.perscholas.furniturehaven.model.CartItem;
import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.service.CartService;
import org.perscholas.furniturehaven.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Optional;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/add-cart")
    public String addCart(@RequestParam Long productId, @RequestParam int quantity, @RequestParam long customerId, Model model) {

        cartService.addToCart(customerId, productId, quantity);


        return "redirect:/cart";
    }

    @GetMapping("/cart")
    public String viewCart(Model model) {
        Optional<Customer> customer = getCurrentCustomer();
        Cart cart=cartService.findByCustomerId(customer.get().getId());
        model.addAttribute("cart", cart);

        return "view-cart";
    }

    public Optional<Customer> getCurrentCustomer() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        System.out.println(authentication.getPrincipal());
        String userName = ((UserDetails) authentication.getPrincipal()).getUsername();
        System.out.println(userName);
        System.out.println("************777*******");

        if (authentication != null && authentication.getPrincipal() != null)
            return customerService.findByUsername(userName);
        return null;
    }
}
