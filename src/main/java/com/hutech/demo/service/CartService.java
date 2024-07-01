package com.hutech.demo.service;

import com.hutech.demo.model.CartItem;
import com.hutech.demo.model.Product;
import com.hutech.demo.model.Voucher;
import com.hutech.demo.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.annotation.SessionScope;

import java.util.ArrayList;
import java.util.List;

@Service
@SessionScope
public class CartService {
    private List<CartItem> cartItems = new ArrayList<>();

    @Autowired
    private ProductRepository productRepository;
    @Autowired
    private VoucherService voucherService;

    public void addToCart(Long productId, int quantity) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product not found: " + productId));
        cartItems.add(new CartItem(product, quantity));
    }

    public List<CartItem> getCartItems() {
        return cartItems;
    }

    public void removeFromCart(Long productId) {
        cartItems.removeIf(item -> item.getProduct().getId().equals(productId));
    }

    public void clearCart() {
        cartItems.clear();
    }

    // Method to apply voucher discount to the cart
    public double applyVoucherDiscount(Voucher voucher) {
        // Calculate total amount of cart items
        double totalAmount = calculateTotalAmount(cartItems);

        // Apply voucher discount logic
        double discountAmount = voucher.getDiscountAmount().doubleValue();
        totalAmount -= discountAmount;

        // Ensure total amount doesn't go below zero
        return totalAmount < 0 ? 0 : totalAmount;
    }

    // Calculate total amount of cart items
    private double calculateTotalAmount(List<CartItem> cartItems) {
        return cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
    }


}