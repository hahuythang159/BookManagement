package com.hutech.demo.controller;

import com.hutech.demo.model.Product;
import com.hutech.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;

import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;


@Controller
@RequestMapping("/home")
public class HomeController {

    @Autowired
    private ProductService productService;

    @GetMapping
    public String showHomePage(Model model) {
        model.addAttribute("products", productService.getAllProducts());
        return "/home/index";
    }
    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable("id") Long id, Model model) {
        Optional<Product> productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            model.addAttribute("product", product);

            return "/products/products-detail"; // Return the HTML template for product detail page
        } else {
            return "redirect:/home"; // Redirect to product list if product not found
        }
    }
    //Tìm kiếm sản phẩm
    @GetMapping("/search")
    public String searchProducts(@RequestParam("query") String query, Model model) {
        List<Product> products = productService.searchProducts(query);
        model.addAttribute("products", products);
        model.addAttribute("title", "Products List");
        return "home/index"; // Đảm bảo rằng tên của template là "products/list"
    }

}
