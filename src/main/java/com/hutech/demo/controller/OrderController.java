package com.hutech.demo.controller;

import com.hutech.demo.model.CartItem;
import com.hutech.demo.model.Order;
import com.hutech.demo.model.Product;
import com.hutech.demo.service.CartService;
import com.hutech.demo.service.OrderService;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;

    @GetMapping("/checkout")
    public String checkout() {
        return "/cart/checkout";
    }
    @PostMapping("/submit")
    public String submitOrder(String customerName, String StreetAddress, String PhoneNumber, String email, String note, String thanhToan) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; // Redirect if cart is empty
        }
        orderService.createOrder(customerName, StreetAddress, PhoneNumber, email, note, thanhToan, cartItems);
        return "redirect:/order/confirmation";
    }
    @GetMapping
    public String showOrderList(Model model) {
        model.addAttribute("orders", orderService.getAllOrders());
        return "/orders/order-list";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Model model) {
        model.addAttribute("message", "Your order has been successfully placed.");
        return "cart/order-confirmation";
    }
    @GetMapping("/view/{orderId}")
    public String viewOrderDetails(@PathVariable Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return "redirect:/order"; // Redirect if order not found
        }
        model.addAttribute("order", order);
        return "orders/order-details";
    }
}
