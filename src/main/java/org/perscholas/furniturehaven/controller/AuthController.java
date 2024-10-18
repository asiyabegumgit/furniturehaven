package org.perscholas.furniturehaven.controller;

import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.perscholas.furniturehaven.model.Category;
import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.service.CategoryService;
import org.perscholas.furniturehaven.service.CustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;
@Slf4j
@Controller
public class AuthController {

    @Autowired
    private CustomerService customerService;
    @Autowired
    private CategoryService categoryService;

    @GetMapping({"/", "/home"})
    public String showHomePage(Model model) {
        log.info("Accessing home page");
        List<Category> categories = categoryService.getAllCategories();
        log.debug("Retrieved {} categories for home page", categories.size());
        model.addAttribute("categories", categories);
        return "homepage";
    }

    @GetMapping("/login")
    public String showLoginForm() {
        log.info("Showing login form");
        return "login";
    }

    @GetMapping("/faq")
    public String showFAQPage(Model model) {
        log.info("Accessing FAQ page");
        return "faq";
    }

    @GetMapping("/about")
    public String showAboutPage(Model model) {
        log.info("Accessing About page");
        return "about";
    }

    @GetMapping("/contact")
    public String showContactPage(Model model) {
        log.info("Accessing Contact page");
        return "contact";
    }

    @GetMapping("/return-policy")
    public String showServicesPage(Model model) {
        log.info("Accessing Return Policy page");
        return "return-policy";
    }

    @GetMapping("/privacy")
    public String showPrivacyPage(Model model) {
        log.info("Accessing Privacy Policy page");
        return "privacy";
    }

    @GetMapping("/shipping")
    public String showShippingPage(Model model) {
        log.info("Accessing Shipping Information page");
        return "shipping";
    }

    @GetMapping("/signup")
    public String showRegistrationForm(Model model) {
        log.info("Showing registration form");
        model.addAttribute("customer", new Customer());
        return "signup";
    }

    @PostMapping("/signup")
    public String saveCustomer(@ModelAttribute("customer") Customer customer) {
        log.info("Processing customer registration");
        try {
            customerService.saveCustomer(customer);
            log.info("Customer registered successfully: {}", customer.getUsername());
            return "redirect:/login";
        } catch (Exception e) {
            log.error("Error occurred while registering customer", e);
            // You might want to add error handling here, such as adding an error attribute to the model
            // and returning to the registration page with an error message
            return "signup";
        }
    }
}
