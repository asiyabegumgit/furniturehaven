package org.perscholas.furniturehaven.controller;

import org.perscholas.furniturehaven.model.Category;
import org.perscholas.furniturehaven.model.Product;

import org.perscholas.furniturehaven.service.CategoryService;
import org.perscholas.furniturehaven.service.ProductService;
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

@Controller
@RequestMapping("/products")
public class ProductController {
    @Autowired
    private ProductService productService;
   @Autowired
   private CategoryService categoryService;
    @PostMapping("/search")
    public String searchByKeyword(@RequestParam("keyword") String keyword, @RequestParam("categoryId") Long categoryId,Model model) {
        List<Product> productPage =  productService.searchByKeyword(keyword,categoryId);
        model.addAttribute("products",productPage);
        List<Category> categories=categoryService.getAllCategories();
        model.addAttribute("categories", categories);

        return "admin-page";
    }
    @GetMapping("/search")
    public String searchProductsByKeyword(@RequestParam("keyword") String keyword, @RequestParam("categoryId") Long categoryId,Model model) {
        List<Product> productPage =  productService.searchByKeyword(keyword,categoryId);
        model.addAttribute("products",productPage);

        return "products-list";
    }

    @GetMapping("/categories")
    public String getProductsByCategory(@RequestParam("categoryId") Long categoryId,@RequestParam(defaultValue = "0") int page,
                                         @RequestParam(defaultValue = "6") int size, Model model)

    {
        Pageable pageable= PageRequest.of(page,size);
        Page<Product> productPage=productService.getProductsByCategory(categoryId,pageable);
        model.addAttribute("products",productPage);
        model.addAttribute("categoryId",categoryId);
        return "products-list";
}
    @GetMapping
    public String viewAllProducts(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "9") int size, Model model) {

        Page<Product> productPage = productService.getAllProducts(page, size);
        model.addAttribute("products",productPage);
        return "products-list";

    }

    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Optional<Product> product=productService.getProductById(id);
        if(product.isPresent()) {
            model.addAttribute("product", product.get());
            //Optional<Customer> customer = getCurrentCustomer();

            /*if(customer.isPresent()) {
                model.addAttribute("customer", customer.get());
            }

        }*/}
        else{
            return "redirect:/products";
        }

        return "product-detail";
    }
    @PostMapping
    public String handleProductAction(@RequestParam("action") String action,@RequestParam("productId") Long productId,Model model) {
        Optional<Product> optionalProduct=productService.getProductById(productId);

        if(action.equals("edit")) {
            if(optionalProduct.isPresent()) {
                Product product=optionalProduct.get();
                model.addAttribute("product", product);
                }

            return "product-form";
        }
        else{
            productService.deleteProduct(productId);

        }
        return "redirect:/admin";


    }
    @PostMapping("/update")
    public String updateProduct(@RequestParam("id") Long productId,@ModelAttribute("product") Product product) {
        productService.updateProduct(productId, product);
        return "redirect:/admin";
    }
    @GetMapping("/upload")
    public String showProductUploadForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-upload";
    }
    @PostMapping("/upload")
    public String uploadProductsCSV(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload");
            return "redirect:/admin";
        }
        try {
            List<Product> products=parseCSV(file);
            productService.saveProducts(products);
            model.addAttribute("success", "Successfully uploaded " + products.size() + " products");
        }
        catch (Exception e) {
            model.addAttribute("error","An error occurred while uploading the file");
            e.printStackTrace();
        }
        return "redirect:/admin";
    }
    private List<Product> parseCSV(MultipartFile file) throws IOException
    {
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
                products.add(product);
            }
        }
        return products;
    }

}
