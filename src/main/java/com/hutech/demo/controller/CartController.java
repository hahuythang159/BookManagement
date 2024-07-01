package com.hutech.demo.controller;


import com.hutech.demo.model.CartItem;
import com.hutech.demo.model.Voucher;
import com.hutech.demo.service.CartService;
import com.hutech.demo.service.OrderService;
import com.hutech.demo.service.VoucherService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;
    @Autowired
    private OrderService orderService;

    @Autowired
    private VoucherService voucherService;

    @GetMapping
    public String showCart(Model model) {
        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("cartItems", cartItems);

        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();
        model.addAttribute("totalAmount", totalAmount);
        List<Voucher> vouchers = voucherService.getAllVouchers();
        model.addAttribute("vouchers", vouchers);

        return "cart/cart";
    }


    @PostMapping("/add")
    public String addToCart(@RequestParam Long productId, @RequestParam int quantity) {
        cartService.addToCart(productId, quantity);
        return "redirect:/cart";
    }

    @GetMapping("/remove/{productId}")
    public String removeFromCart(@PathVariable Long productId) {
        cartService.removeFromCart(productId);
        return "redirect:/cart";
    }

    @GetMapping("/clear")
    public String clearCart() {
        cartService.clearCart();
        return "redirect:/cart";
    }

    @PostMapping("/applyVoucher")
    public String applyVoucher(@RequestParam Long voucherId, Model model) {
        Optional<Voucher> voucherOptional = voucherService.getVoucherById(voucherId);
        if (voucherOptional.isPresent()) {
            Voucher voucher = voucherOptional.get();
            double totalAmountAfterDiscount = cartService.applyVoucherDiscount(voucher);
            model.addAttribute("totalAmount", totalAmountAfterDiscount);
            model.addAttribute("voucherApplied", true);
            model.addAttribute("voucherMessage", "Voucher applied successfully!");
        } else {
            model.addAttribute("voucherError", "Invalid voucher selected");
        }

        List<CartItem> cartItems = cartService.getCartItems();
        model.addAttribute("cartItems", cartItems);

        List<Voucher> vouchers = voucherService.getAllVouchers();
        model.addAttribute("vouchers", vouchers);

        return "cart/cart";
    }
}

