package org.perscholas.furniturehaven.controller;

import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.model.Role;
import org.perscholas.furniturehaven.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class AuthController {

    @Autowired
    private CustomerService customerService;

    // Show the login form
    @GetMapping({"/login","/logout"})
    public String showLoginForm() {
        return "login";
    }

    // Show the landing page
   @GetMapping({"/", "/homepage"})
    public String showHomePage(Model model) {

        return "homepage";
    }
    // Show the FAQ page
    @GetMapping( "/faq")
    public String showFAQPage(Model model) {

        return "faq";
    }
    // Show the registration page
    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
        model.addAttribute("customer", new Customer());  // Pass an empty Employee object to the form
        return "signup";
    }

    // Handle registration form submission
    @PostMapping("/signup")
    public String saveCustomer(@ModelAttribute("customer") Customer customer) {
        // Assign the role based on the form selection (either CUSTOMER or ADMIN)

        customerService.saveCustomer(customer);// Save the user with their selected role
        return "redirect:/login";  // Redirect to the login page
    }



}