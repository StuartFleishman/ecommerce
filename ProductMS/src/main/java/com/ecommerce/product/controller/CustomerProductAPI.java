package com.ecommerce.product.controller;

import com.ecommerce.product.dto.ProductDTO;
import com.ecommerce.product.service.CustomerProductService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RequestMapping(value = "/product-api")
@RestController
@CrossOrigin
@Validated
public class CustomerProductAPI {

    @Autowired
    private CustomerProductService customerProductService;

    @Autowired
    private Environment environment;


    Log logger = LogFactory.getLog(CustomerProductAPI.class);

    @GetMapping(value = "/products")
    public ResponseEntity<List<ProductDTO>> getAllProducts() throws Exception {
        List<ProductDTO> productDTOS = customerProductService.getAllProducts();
        return new ResponseEntity<>(productDTOS, HttpStatus.OK);
    }

    @GetMapping(value = "/product/{productId}")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Integer productId) throws Exception {
        System.out.println("==============================IN PRODUCT_MS==============================");
        ProductDTO productDTO = customerProductService.getProductById(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }

    @PutMapping(value = "/update/{productId}")
    public ResponseEntity<String> reduceAvailableQuantity(@PathVariable Integer productId ,
                                                          @RequestBody Integer quantity) throws Exception {
        customerProductService.reduceAvailableQuantity(productId, quantity);
        String message = environment.getProperty("ProductAPI.REDUCE_QUANTITY_SUCCESSFULL");
        return new ResponseEntity<>(message, HttpStatus.OK);

    }
}