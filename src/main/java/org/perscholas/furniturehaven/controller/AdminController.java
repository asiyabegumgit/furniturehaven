package org.perscholas.furniturehaven.controller;

import lombok.extern.slf4j.Slf4j;
import org.perscholas.furniturehaven.model.Category;
import org.perscholas.furniturehaven.model.Product;
import org.perscholas.furniturehaven.service.CategoryService;
import org.perscholas.furniturehaven.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
@Slf4j
@Controller
public class AdminController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/admin")
    public String showAdminPage(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "9") int size,
                                Model model) {
        log.info("Accessing admin page with parameters: page={}, size={}", page, size);

        try {
            log.debug("Fetching products from ProductService");
            Page<Product> products = productService.getAllProducts(page, size);
            log.info("Retrieved {} products for page {}", products.getNumberOfElements(), page);
            model.addAttribute("products", products);

            log.debug("Fetching categories from CategoryService");
            List<Category> categories = categoryService.getAllCategories();
            log.info("Retrieved {} categories", categories.size());
            model.addAttribute("categories", categories);

            log.debug("Adding attributes to model: products, categories");
            log.info("Successfully prepared admin page model");
            return "admin-page";
        } catch (Exception e) {
            log.error("Error occurred while preparing admin page", e);
            return "error";
        }
    }
}

