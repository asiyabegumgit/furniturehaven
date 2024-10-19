package org.perscholas.furniturehaven.controller;

import lombok.extern.slf4j.Slf4j;
import org.perscholas.furniturehaven.model.Category;
import org.perscholas.furniturehaven.model.Product;

import org.perscholas.furniturehaven.service.CategoryService;
import org.perscholas.furniturehaven.service.ProductService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;


import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
@Slf4j
@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
    @Autowired
    private CategoryService categoryService;
    @PostMapping("/search")
    public String searchByKeyword(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "6") int size,@RequestParam("keyword") String keyword, @RequestParam("categoryId") Long categoryId,Model model) {
        log.info("Searching products with keyword: '{}', categoryId: {}, page: {}, size: {}", keyword, categoryId, page, size);
        Pageable pageable= PageRequest.of(page,size);
        Page<Product> productPage=productService.searchByKeyword(keyword,categoryId,pageable);
        log.debug("Found {} products matching the search criteria", productPage.getTotalElements());
        model.addAttribute("products",productPage);
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);
        model.addAttribute("keyword", keyword);
       model.addAttribute("categoryId", categoryId);

        return "admin-page";
    }
    @GetMapping("/search")
    public String searchProductsByKeyword(@RequestParam(defaultValue = "0") int page,
                                          @RequestParam(defaultValue = "6") int size,@RequestParam("keyword") String keyword,@RequestParam("categoryId") Long categoryId,Model model) {
        log.info("Searching products for customers with keyword: '{}', categoryId: {}, page: {}, size: {}", keyword, categoryId, page, size);
        Pageable pageable= PageRequest.of(page,size);
        Page<Product> productPage =  productService.searchByKeyword(keyword,categoryId,pageable);
        log.debug("Found {} products matching the search criteria", productPage.getTotalElements());
        model.addAttribute("products",productPage);
        model.addAttribute("keyword", keyword);
        model.addAttribute("categoryId", categoryId);
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "products-list";
    }

    @GetMapping("/categories")
    public String getProductsByCategory(@RequestParam("categoryId") Long categoryId,@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "6") int size, Model model)

    {
        log.info("Fetching products for category: {}, page: {}, size: {}", categoryId, page, size);
        Pageable pageable= PageRequest.of(page,size);
        Page<Product> productPage=productService.getProductsByCategory(categoryId,pageable);
        model.addAttribute("products",productPage);
        model.addAttribute("categoryId",categoryId);
        List<Category> categories=categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "products-list";
}
    @GetMapping
    public String viewAllProducts(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "9") int size, Model model) {
        log.info("Fetching all products, page: {}, size: {}", page, size);
        Page<Product> productPage = productService.getAllProducts(page, size);
        log.debug("Retrieved {} products", productPage.getTotalElements());
        model.addAttribute("products",productPage);
        List<Category> categories=categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "products-list";

    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        log.info("Viewing product details for id: {}", id);
        Optional<Product> product=productService.getProductById(id);
        if(product.isPresent()) {
            log.debug("Product found: {}", product.get());
            model.addAttribute("product", product.get());
            }
        else{
            log.warn("Product not found for id: {}", id);
            return "redirect:/products";
        }
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "product-detail";
    }
    @PostMapping
    public String handleProductAction(@RequestParam("action") String action,@RequestParam("productId") Long productId,Model model) {
        log.info("Handling product action: {}, for productId: {}", action, productId);
        Optional<Product> optionalProduct=productService.getProductById(productId);
        if(action.equals("edit")) {
            if(optionalProduct.isPresent()) {
                Product product=optionalProduct.get();
                log.debug("Editing product: {}", product);
                model.addAttribute("product", product);
                return "product-form";
                }
            else{
                log.warn("Product not found for editing, id: {}", productId);
                return "redirect:/products";
            }

        }
        else {
            log.info("Deleting product with id: {}", productId);
            productService.deleteProduct(productId);
            log.info("Product with id:{} deleted", productId);
        }
           List<Category> categories = categoryService.getAllCategories();
           model.addAttribute("categories", categories);

           return "redirect:/admin";


    }
    @PostMapping("/update")
    public String updateProduct(@ModelAttribute("product") Product product) {
        log.info("Updating product with id: {}", product.getId());
        productService.updateProduct(product.getId(), product);
        log.info("Product updated successfully: {}", product);
        return "redirect:/admin";
    }
    @GetMapping("/upload")
    public String showProductUploadForm(Model model) {
        log.info("Showing product upload form");
        model.addAttribute("product", new Product());
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "product-upload";
    }
    @PostMapping("/upload")
    public String uploadProductsCSV(@RequestParam("file") MultipartFile file, Model model) {
        log.info("Uploading products CSV file: {}", file.getOriginalFilename());
        if (file.isEmpty()) {
            log.warn("Empty file uploaded");
            model.addAttribute("error", "Please select a file to upload");
            return "redirect:/admin";
        }
        try {
            List<Product> products=parseCSV(file);
            productService.saveProducts(products);
            log.info("Successfully uploaded {} products", products.size());
            model.addAttribute("success", "Successfully uploaded " + products.size() + " products");
        }
        catch (Exception e) {
            log.error("Error occurred while uploading file", e);
            model.addAttribute("error","An error occurred while uploading the file");
            e.printStackTrace();
        }
        List<Category> categories = categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "redirect:/admin";
    }
    private List<Product> parseCSV(MultipartFile file) throws IOException
    {
        log.debug("Parsing CSV file: {}", file.getOriginalFilename());
        List<Product> products=new ArrayList<>();
        try(BufferedReader br=new BufferedReader(new InputStreamReader(file.getInputStream()))){
            String line;
            br.readLine();
            while((line=br.readLine())!=null)
            {
                String[] tokens=line.split(",");
                Product product=new Product();
                product.setName(tokens[0]);
                product.setDescription(tokens[1]);
                product.setPrice(Double.parseDouble(tokens[2]));
                product.setCategoryId(Long.parseLong(tokens[3]));
                product.setBrand(tokens[4]);
                product.setImage(tokens[5]);
                product.setRating(tokens[6]);
                products.add(product);
            }
        }
        log.debug("Parsed {} products from CSV", products.size());
        return products;
    }

}
