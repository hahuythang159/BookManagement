package com.hutech.demo.model;
import jakarta.persistence.*;
import lombok.*;
import java.util.List;
@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="orders")
public class Order {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String customerName;
    private String StreetAddress;
    private String PhoneNumber;
    private String email;
    private String note;
    private String thanhToan;
    private double totalPrice;
    private double totalAmount;
    @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    private List<OrderDetail> orderDetails;
    public void setTotalAmount(double totalAmount) {
        this.totalAmount = totalAmount;
    }
}