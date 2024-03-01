package com.yodsarun.demo.spring.interview.repository;

import com.yodsarun.demo.spring.interview.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<CustomerEntity, Long> {
    CustomerEntity findCustomerEntityByName(String name);
}
