package com.hutech.demo.controller;

import com.hutech.demo.model.Product;
import com.hutech.demo.service.CategoryService;
import com.hutech.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;

    // Display a list of all products
    @GetMapping
    public String showProductList(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/products/product-list";
    }

    // For adding a new product
    @GetMapping("/add")
    public String showAddForm(Model model) {
        model.addAttribute("product", new Product());
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/products/add-product";
    }

    // Process the form for adding a new product

    @PostMapping("/add")
    public String addProduct(@Valid Product newProduct, BindingResult result, @RequestParam MultipartFile imageProduct, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "/products/add-product";  // Ensure the path matches your Thymeleaf template
        }

        // Handle image file
        if (imageProduct != null && imageProduct.getSize()>0) {
            try {
                File saveFile=new ClassPathResource("static/images").getFile();
                String newImageFile=UUID.randomUUID()+".png";
                Path path  =Paths.get(saveFile.getAbsolutePath()+ File.separator+newImageFile);
                Files.copy(imageProduct.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
                newProduct.setImage(newImageFile);
            } catch (IOException e) {
                e.printStackTrace();
                // Add error logging
            }
        }

        productService.addProduct(newProduct);
        return "redirect:/products";
    }

    // For editing a product
    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable Long id, Model model) {
        Product product = productService.getProductById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
        model.addAttribute("product", product);
        model.addAttribute("categories", categoryService.getAllCategories());
        return "/products/update-product";
    }

    // Process the form for updating a product
    @PostMapping("/update/{id}")
    public String updateProduct(@PathVariable Long id, @Valid Product product, BindingResult result,@RequestParam MultipartFile imageProduct, Model model) {
        if (result.hasErrors()) {
            product.setId(id);
            return "/products/update-product";
        }
        if (imageProduct != null && imageProduct.getSize()>0) {
            try {
                File saveFile=new ClassPathResource("static/images").getFile();
                String newImageFile=UUID.randomUUID()+".png";
                Path path  =Paths.get(saveFile.getAbsolutePath()+ File.separator+newImageFile);
                Files.copy(imageProduct.getInputStream(),path,StandardCopyOption.REPLACE_EXISTING);
                product.setImage(newImageFile);
            } catch (IOException e) {
                e.printStackTrace();
                // Add error logging
            }
        }
            else{
                Product existingProduct = productService.getProductById(id).orElseThrow(() -> new IllegalArgumentException("Invalid product Id:" + id));
                product.setImage(existingProduct.getImage());
            }

        productService.updateProduct(product);
        return "redirect:/products";
    }

    // Handle request to delete a product
    @GetMapping("/delete/{id}")
    public String deleteProduct(@PathVariable Long id) {
        productService.deleteProductById(id);
        return "redirect:/products";
    }
}
