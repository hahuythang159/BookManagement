package com.hutech.demo.service;

import com.hutech.demo.model.OrderDetail;
import com.hutech.demo.model.Product;
import com.hutech.demo.repository.OrderDetailRepository;
import com.hutech.demo.repository.ProductRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.text.NumberFormat;
import java.util.*;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductService {

    private final ProductRepository productRepository;
    private final OrderDetailRepository orderDetailRepository;

    public List<Product> getAllProducts() {
        return productRepository.findAll();
    }

    public Optional<Product> findById(Long id) {
        return productRepository.findById(id);
    }

    public Optional<Product> getProductById(Long id) {
        return productRepository.findById(id);
    }


    public Product addProduct(Product product) {
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalStateException("Product with ID " + product.getId() + " does not exist."));
        existingProduct.setName(product.getName());
        existingProduct.setPrice(product.getPrice());
        existingProduct.setAuthor(product.getAuthor());
        existingProduct.setDescription(product.getDescription());
        existingProduct.setImage(product.getImage());
        existingProduct.setCategory(product.getCategory());

        return productRepository.save(existingProduct);
    }

    public void deleteProductById(Long id) {
        if (!productRepository.existsById(id)) {
            throw new IllegalStateException("Product with ID " + id + " does not exist.");
        }
        orderDetailRepository.deleteByProductId(id);
        productRepository.deleteById(id);
    }

    public Map<Product, Integer> getProductSales() {
        List<OrderDetail> orderDetails = orderDetailRepository.findAll();
        Map<Product, Integer> productSales = new HashMap<>();

        for (OrderDetail orderDetail : orderDetails) {
            Product product = orderDetail.getProduct();
            int quantity = orderDetail.getQuantity();
            productSales.put(product, productSales.getOrDefault(product, 0) + quantity);
        }

        return productSales;
    }

    public Map<Product, String> getFormattedProductSalesAmount(Map<Product, Integer> productSales) {
        Map<Product, String> productSalesAmount = new HashMap<>();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));

        for (Map.Entry<Product, Integer> entry : productSales.entrySet()) {
            Product product = entry.getKey();
            int quantity = entry.getValue();
            double amount = product.getPrice() * quantity;
            productSalesAmount.put(product, currencyFormat.format(amount));
        }

        return productSalesAmount;
    }

    public String getFormattedTotalSalesAmount(Map<Product, Integer> productSales) {
        double totalAmount = productSales.entrySet().stream()
                .mapToDouble(entry -> entry.getKey().getPrice() * entry.getValue())
                .sum();
        NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(new Locale("vi", "VN"));
        return currencyFormat.format(totalAmount);
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCase(query);
    }

}
