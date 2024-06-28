package com.hutech.demo.controller;

import com.hutech.demo.model.CartItem;
import com.hutech.demo.model.Order;
import com.hutech.demo.model.Product;
import com.hutech.demo.repository.OrderRepository;
import org.springframework.http.HttpHeaders;
import com.hutech.demo.service.CartService;
import com.hutech.demo.service.ExcelExportService;
import com.hutech.demo.service.OrderService;
import com.hutech.demo.service.VNPAYService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.ui.Model;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.ByteArrayInputStream;
import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;
    @Autowired
    private VNPAYService vnPayService;
    @Autowired
    private ExcelExportService excelExportService;

    @GetMapping("/checkout")
    public String checkout() {
        return "/cart/checkout";
    }
    @PostMapping("/submit")
    public String submitOrder(String customerName, String StreetAddress, String PhoneNumber, String email, String note, String thanhToan, HttpServletRequest request) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; // Redirect if cart is empty
        }

        double totalAmount = cartItems.stream()
                .mapToDouble(item -> item.getProduct().getPrice() * item.getQuantity())
                .sum();

        if ("online".equals(thanhToan)) {
            // Xử lý thanh toán bằng VNPay
            String baseUrl = request.getScheme() + "://" + request.getServerName() + ":" + request.getServerPort();
            String vnpayUrl = vnPayService.createOrder(request, (int) totalAmount, "Thanh toán giỏ hàng", baseUrl);
            return "redirect:" + vnpayUrl;
        } else {
            // Xử lý thanh toán tiền mặt
            orderService.createOrder(customerName, StreetAddress, PhoneNumber, email, note, thanhToan, cartItems);
            return "redirect:/order/confirmation";
        }
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
        double totalAmount = orderService.calculateTotalAmount(order);

        model.addAttribute("order", order);
        model.addAttribute("totalAmount", totalAmount);
        return "orders/order-details";
    }
    @GetMapping("/vnpay-payment-return")
    public String paymentCompleted(HttpServletRequest request, Model model) {
        int paymentStatus = vnPayService.orderReturn(request);

        String orderInfo = request.getParameter("vnp_OrderInfo");
        String paymentTime = request.getParameter("vnp_PayDate");
        String transactionId = request.getParameter("vnp_TransactionNo");
        String totalPrice = request.getParameter("vnp_Amount");

        model.addAttribute("orderId", orderInfo);
        model.addAttribute("totalPrice", totalPrice);
        model.addAttribute("paymentTime", paymentTime);
        model.addAttribute("transactionId", transactionId);

        return paymentStatus == 1 ? "ordersuccess" : "orderfail";
    }

    // Xuất danh sách đơn hàng ra file Excel
    @GetMapping("/export")
    public ResponseEntity<InputStreamResource> exportOrderListToExcel() {
        List<Order> orders = orderService.getAllOrders();
        ByteArrayInputStream in = excelExportService.exportOrderListToExcel(orders);

        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Disposition", "attachment; filename=orders.xlsx");

        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType("application/vnd.ms-excel"))
                .body(new InputStreamResource(in));
    }

}
