package com.hutech.demo.controller;

import com.hutech.demo.model.Product;
import com.hutech.demo.service.CategoryService;
import com.hutech.demo.service.ExcelExportService;
import com.hutech.demo.service.ProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.HttpHeaders;

import jakarta.validation.Valid;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/products")
public class ProductController {

    @Autowired
    private ProductService productService;

    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ExcelExportService excelExportService;


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

    // Process the form for adding a new product
    @PostMapping("/add")
    public String addProduct(@Valid Product newProduct, BindingResult result, @RequestParam MultipartFile imageProduct, Model model) {
        if (result.hasErrors()) {
            model.addAttribute("categories", categoryService.getAllCategories());
            return "/products/add-product";
        }

        // Handle image file
        if (imageProduct != null && imageProduct.getSize() > 0) {
            try {
                File saveFile = new ClassPathResource("static/images").getFile();
                String newImageFile = UUID.randomUUID() + ".png";
                Path path = Paths.get(saveFile.getAbsolutePath() + File.separator + newImageFile);
                Files.copy(imageProduct.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
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
    public String deleteProduct(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            productService.deleteProductById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Product deleted successfully.");
        } catch (IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("errorMessage", e.getMessage());
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "An error occurred while deleting the product.");
        }
        return "redirect:/products";
    }
    @GetMapping("/detail/{id}")
    public String showProductDetail(@PathVariable("id") Long id, Model model) {
        Optional<Product> productOptional = productService.findById(id);
        if (productOptional.isPresent()) {
            Product product = productOptional.get();
            model.addAttribute("product", product);

            return "/products/products-detail"; // Return the HTML template for product detail page
        } else {
            return "redirect:/products"; // Redirect to product list if product not found
        }
    }

    //Quản lý doanh thu
    @GetMapping("/sales")
    public String showProductSales(Model model) {
        Map<Product, Integer> productSales = productService.getProductSales();
        Map<Product, String> productSalesAmount = productService.getFormattedProductSalesAmount(productSales);
        String totalSalesAmount = productService.getFormattedTotalSalesAmount(productSales);

        model.addAttribute("sales", productSales);
        model.addAttribute("salesAmount", productSalesAmount);
        model.addAttribute("totalSalesAmount", totalSalesAmount);
        return "/products/product-sales";
    }

    //Tìm kiếm sản phẩm
    @GetMapping("/search")
    public String searchProducts(@RequestParam("query") String query, Model model) {
        List<Product> products = productService.searchProducts(query);
        model.addAttribute("products", products);
        model.addAttribute("title", "Products List");
        return "/products/product-list";
    }

    //xuat file excel

    @PreAuthorize("hasAuthority('ADMIN')")
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportProductListToExcel() {
        List<Product> products = productService.getAllProducts();
        ByteArrayInputStream in = excelExportService.exportProductListToExcel(products);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=products.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(in));
    }

}
