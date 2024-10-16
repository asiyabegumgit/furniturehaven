package org.perscholas.furniturehaven.controller;

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
@Controller
public class AdminController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @GetMapping("/admin")
    public String showAdminPage(@RequestParam(defaultValue = "0") int page,
                                @RequestParam(defaultValue = "9") int size,Model model) {
        Page<Product> products=productService.getAllProducts(page,size);
        model.addAttribute("products",products);
        List<Category> categories=categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        return "admin-page";
    }
}
