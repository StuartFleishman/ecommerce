package com.ecommerce.cart.repository;

import com.ecommerce.cart.entity.CartProduct;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface CartProductRepository extends CrudRepository<CartProduct, Integer> {
}