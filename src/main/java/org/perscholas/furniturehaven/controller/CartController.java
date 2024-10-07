package org.perscholas.furniturehaven.controller;

import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.service.CartService;
import org.perscholas.furniturehaven.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class CartController {
    @Autowired
    private CartService cartService;
    @Autowired
    private CustomerService customerService;

    @PostMapping("/add-cart")
    public String addToCart(@RequestParam("productId") Long productId,
                            @RequestParam("customer") Long customerId,
                            @RequestParam("quantity") int quantity, Model model) {
        Customer customer=customerService.findById(customerId);
        model.addAttribute("customer",customer);
        model.addAttribute("productId",productId);
        cartService.addProductToCart(customer, productId, quantity);
        return "redirect:/cart";
    }
}
