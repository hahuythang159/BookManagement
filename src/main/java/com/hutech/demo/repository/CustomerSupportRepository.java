package com.hutech.demo.repository;

import com.hutech.demo.model.CustomerSupport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerSupportRepository extends JpaRepository<CustomerSupport, Long> {
    // Các phương thức truy vấn có thể được thêm vào nếu cần
}

