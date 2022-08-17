
package com.ecommerce.customer.service;

import com.ecommerce.customer.dto.CustomerDTO;
import com.ecommerce.customer.entity.Customer;
import com.ecommerce.customer.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

@Service(value = "customerService")
@Transactional
public class CustomerServiceImpl implements CustomerService{

    @Autowired
    private CustomerRepository customerRepository;

    @Autowired
    Environment environment;

    
    @Override
    public CustomerDTO authenticateCustomer(String emailId, String password) throws Exception {
        CustomerDTO customerDTO = null;

     
        Optional<Customer> optionalCustomer = customerRepository.findById(emailId.toLowerCase());
        Customer customer = optionalCustomer.orElseThrow(() ->
                new Exception(environment.getProperty("CustomerService.CUSTOMER_NOT_FOUND")));
        
        if (!password.equals(customer.getPassword()))
            throw new Exception(environment.getProperty("CustomerService.INVALID_CREDENTIALS"));
        customerDTO = new CustomerDTO();
        customerDTO.setEmailId(customer.getEmailId());
        customerDTO.setName(customer.getName());
        customerDTO.setPhoneNumber(customer.getPhoneNumber());
        customerDTO.setAddress(customer.getAddress());
        return customerDTO;
    }

   
    @Override
    public String registerNewCustomer(CustomerDTO customerDTO) throws Exception {
        String registeredWithEmailId = null;
        boolean isEmailNotAvailable = customerRepository.findById(customerDTO.getEmailId().toLowerCase()).isEmpty();
        boolean isPhoneNumberNotAvailable = customerRepository.findByPhoneNumber(customerDTO.getPhoneNumber()).isEmpty();
        if (isEmailNotAvailable) {
            if (isPhoneNumberNotAvailable) {
                Customer customer = new Customer();
                customer.setEmailId(customerDTO.getEmailId().toLowerCase());
                customer.setName(customerDTO.getName());
                customer.setPassword(customerDTO.getPassword());
                customer.setPhoneNumber(customerDTO.getPhoneNumber());
                customer.setAddress(customerDTO.getAddress());
                customerRepository.save(customer);
                registeredWithEmailId = customer.getEmailId();
            } else {
                throw new Exception(environment.getProperty("CustomerService.PHONE_NUMBER_ALREADY_IN_USE"));
            }
        } else {
            throw new Exception(environment.getProperty("CustomerService.EMAIL_ID_ALREADY_IN_USE"));
        }
        return registeredWithEmailId;

    }

   
    @Override
    public void updateShippingAddress(String customerId , String address) throws Exception {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerId.toLowerCase());
        Customer customer = optionalCustomer.orElseThrow(() ->
                new Exception(environment.getProperty("CustomerService.CUSTOMER_NOT_FOUND")));
        customer.setAddress(address);
    }

    @Override
    public void deleteShippingAddress(String customerEmailId) throws Exception {
        Optional<Customer> optionalCustomer = customerRepository.findById(customerEmailId.toLowerCase());
        Customer customer = optionalCustomer.orElseThrow(() ->
                new Exception(environment.getProperty("CustomerService.CUSTOMER_NOT_FOUND")));
        customer.setAddress(null);
    }

    @Override
    public CustomerDTO getCustomerByEmailId(String emailId) throws Exception {
        CustomerDTO customerDTO = null;
        Optional<Customer> optionalCustomer = customerRepository.findById(emailId.toLowerCase());
        Customer customer = optionalCustomer.orElseThrow(() ->
                new Exception(environment.getProperty("CustomerService.CUSTOMER_NOT_FOUND")));
        customerDTO = new CustomerDTO();
        customerDTO.setEmailId(customer.getEmailId());
        customerDTO.setName(customer.getName());
        customerDTO.setPhoneNumber(customer.getPhoneNumber());
        customerDTO.setAddress(customer.getAddress());
        return customerDTO;

    }
}