package com.ecommerce.customer.repository;

import java.util.List;

import com.ecommerce.customer.entity.Customer;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CustomerRepository extends CrudRepository<Customer, String> {
	List<Customer> findByPhoneNumber(String phoneNumber);
}