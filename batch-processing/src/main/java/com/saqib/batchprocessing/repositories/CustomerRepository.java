package com.saqib.batchprocessing.repositories;

import com.saqib.batchprocessing.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
}
