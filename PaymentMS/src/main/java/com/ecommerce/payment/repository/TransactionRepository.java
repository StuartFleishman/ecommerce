package com.ecommerce.payment.repository;

import com.ecommerce.payment.entity.Transaction;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TransactionRepository   extends CrudRepository<Transaction, Integer> {
}