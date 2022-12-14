package com.ecommerce.customer.service;

import com.ecommerce.customer.dto.CustomerDTO;

public interface CustomerService {
    CustomerDTO authenticateCustomer(String emailId, String password) throws Exception;
    String registerNewCustomer(CustomerDTO customerDTO) throws Exception;
    void updateShippingAddress(String customerEmailId , String address) throws Exception;
    void deleteShippingAddress(String customerEmailId) throws Exception;
    CustomerDTO getCustomerByEmailId(String emailId) throws Exception;
}