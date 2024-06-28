package com.hutech.demo.model;
import jakarta.persistence.*;
import lombok.*;
@Setter
@Getter
@RequiredArgsConstructor
@AllArgsConstructor
@Entity
@Table(name ="products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    private String name;
    private String author;
    private double price;
    private String description;
    @ManyToOne
    @JoinColumn(name ="category_id")
    private Category category;
    private String image;
    private boolean visible = true; // Mặc định sản phẩm hiển thị
    @Column(nullable = true) // Cho phép sales_count có thể để trống
    private Integer sales_count;

}