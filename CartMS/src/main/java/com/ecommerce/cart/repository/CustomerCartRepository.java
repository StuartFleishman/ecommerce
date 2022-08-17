package com.ecommerce.cart.repository;

import com.ecommerce.cart.entity.CustomerCart;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerCartRepository extends CrudRepository<CustomerCart, Integer> {

    Optional<CustomerCart> findByCustomerEmailId(String customerEmailId);

}