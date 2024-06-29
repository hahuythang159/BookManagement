package com.hutech.demo.service;

import com.hutech.demo.model.CartItem;
import com.hutech.demo.model.Order;
import com.hutech.demo.model.OrderDetail;
import com.hutech.demo.model.Product;
import com.hutech.demo.repository.OrderDetailRepository;
import com.hutech.demo.repository.OrderRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

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

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public Order getOrderById(Long orderId) {
        return orderRepository.findById(orderId).orElse(null);
    }

    @Transactional
    public Order createOrder(String customerName, String streetAddress, String phoneNumber, String email, String note, String thanhToan, List<CartItem> cartItems) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setStreetAddress(streetAddress);
        order.setPhoneNumber(phoneNumber);
        order.setEmail(email);
        order.setNote(note);
        order.setThanhToan(thanhToan);
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

    // Tính tổng số tiền của giỏ hàng
    public double calculateTotalAmount(Order order) {
        double totalAmount = 0.0;
        for (OrderDetail detail : order.getOrderDetails()) {
            double itemTotal = detail.getProduct().getPrice() * detail.getQuantity();
            totalAmount += itemTotal;
        }
        return totalAmount;
    }
}