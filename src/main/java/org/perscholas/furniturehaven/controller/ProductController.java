package org.perscholas.furniturehaven.controller;

import org.perscholas.furniturehaven.model.Customer;
import org.perscholas.furniturehaven.model.Product;
import org.perscholas.furniturehaven.model.User;
import org.perscholas.furniturehaven.service.CartService;
import org.perscholas.furniturehaven.service.CustomerService;
import org.perscholas.furniturehaven.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
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

    @GetMapping("/search")
    public String searchByKeyword(@RequestParam("keyword") String keyword, Model model) {
        List<Product> productPage =  productService.searchByKeyword(keyword);
        model.addAttribute("products",productPage);
        return "products-list";
    }

    @GetMapping
    public String viewAllProducts(@RequestParam(defaultValue = "0") int page,
                                  @RequestParam(defaultValue = "9") int size, Model model) {

        Page<Product> productPage = productService.getProducts(page, size);
        model.addAttribute("products",productPage);
        return"products-list"; // Thymeleaf template name
    }
    @GetMapping("/{id}")
    public String viewProduct(@PathVariable Long id, Model model) {
        Optional<Product> product=productService.findById(id);
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
    @GetMapping("/upload")
    public String showProductUploadForm(Model model) {
        model.addAttribute("product", new Product());
        return "product-upload";
    }
    @PostMapping("/upload")
    public String uploadProductsCSV(@RequestParam("file") MultipartFile file, Model model) {
        if (file.isEmpty()) {
            model.addAttribute("error", "Please select a file to upload");
            return "redirect:/product-upload";
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
        return "redirect:/products";
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
                product.setCategory(tokens[3]);
                product.setBrand(tokens[4]);
                product.setImage(tokens[5]);
                products.add(product);
            }
        }
        return products;
    }

}
