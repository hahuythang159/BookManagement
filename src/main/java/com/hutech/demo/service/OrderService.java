package com.hutech.demo.service;

import com.hutech.demo.model.CartItem;
import com.hutech.demo.model.Order;
import com.hutech.demo.model.OrderDetail;
import com.hutech.demo.model.Voucher;
import com.hutech.demo.repository.OrderDetailRepository;
import com.hutech.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService;
    @Autowired
    private VoucherService voucherService;

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Transactional
    public Order createOrder(String customerName, String streetAddress, String phoneNumber, String email, String note, String thanhToan, List<CartItem> cartItems, String voucherCode) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setStreetAddress(streetAddress);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setNote(note);
        order.setThanhToan(thanhToan);

        double totalAmount = calculateTotalAmount(cartItems);

        // Apply voucher discount if a valid code is provided
        if (voucherCode != null && !voucherCode.isEmpty()) {
            Optional<Voucher> voucherOptional = voucherService.getVoucherByCode(voucherCode);
            if (voucherOptional.isPresent()) {
                Voucher voucher = voucherOptional.get();
                if (voucher.getExpiryDate().isAfter(LocalDate.now())) {
                    totalAmount -= voucher.getDiscountAmount().doubleValue();
                }
            }
        }

        order.setTotalAmount(totalAmount);
        order = orderRepository.save(order);

        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());

            orderDetailRepository.save(detail);
        }

        // Optionally clear the cart after order placement
        cartService.clearCart();

        return order;
    }

    public void save(Order order) {
        orderRepository.save(order);
    }

    // Calculate total amount of the cart items
    public double calculateTotalAmount(List<CartItem> cartItems) {
        double totalAmount = 0.0;
        for (CartItem item : cartItems) {
            double itemTotal = item.getProduct().getPrice() * item.getQuantity();
            totalAmount += itemTotal;
        }
        return totalAmount;
    }

    // Calculate total amount of an order
    public double calculateTotalAmount(Order order) {
        double totalAmount = 0.0;
        for (OrderDetail detail : order.getOrderDetails()) {
            double itemTotal = detail.getProduct().getPrice() * detail.getQuantity();
            totalAmount += itemTotal;
        }
        return totalAmount;
    }
}
