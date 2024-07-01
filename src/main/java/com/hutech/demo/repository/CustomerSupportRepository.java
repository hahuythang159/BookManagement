package com.hutech.demo.repository;

import com.hutech.demo.model.CustomerSupport;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerSupportRepository extends JpaRepository<CustomerSupport, Long> {
}

