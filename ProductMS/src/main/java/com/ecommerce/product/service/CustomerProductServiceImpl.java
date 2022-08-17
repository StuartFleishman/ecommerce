package com.ecommerce.product.service;

import com.ekart.product.dto.ProductDTO;
import com.ekart.product.entity.Product;
import com.ekart.product.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service(value = "CustomerProductService")
@Transactional
public class CustomerProductServiceImpl implements CustomerProductService {
    @Autowired
    private ProductRepository productRepository;

    @Autowired
    Environment environment;

    @Override
    public List<ProductDTO> getAllProducts() throws Exception {
        List<Product> products = (List<Product>) productRepository.findAll();
        List<ProductDTO> list = new ArrayList<>();
        products.forEach(product -> {
            ProductDTO productDTO = new ProductDTO();
            productDTO.setProductId(product.getProductId());
            productDTO.setName(product.getName());
            productDTO.setBrand(product.getBrand());
            productDTO.setCategory(product.getCategory());
            productDTO.setAvailableQuantity(product.getAvailableQuantity());
            productDTO.setDescription(product.getDescription());
            productDTO.setPrice(product.getPrice());
            list.add(productDTO);
        });
        return list;
    }


    @Override
    public ProductDTO getProductById(Integer productId) throws Exception {
        Optional<Product> optional = productRepository.findById(productId);
        Product product = optional.orElseThrow(() ->
                new Exception(environment.getProperty("ProductService.PRODUCT_NOT_AVAILABLE")));
        ProductDTO productDTO = new ProductDTO();
        productDTO.setProductId(product.getProductId());
        productDTO.setName(product.getName());
        productDTO.setBrand(product.getBrand());
        productDTO.setCategory(product.getCategory());
        productDTO.setAvailableQuantity(product.getAvailableQuantity());
        productDTO.setDescription(product.getDescription());
        productDTO.setPrice(product.getPrice());
        return productDTO;
    }


    @Override
    public void reduceAvailableQuantity(Integer productId, Integer quantity) throws Exception {
        Optional<Product> optional = productRepository.findById(productId);
        Product product = optional.orElseThrow(() ->
                new Exception(environment.getProperty("ProductService.PRODUCT_NOT_AVAILABLE")));
        product.setAvailableQuantity(product.getAvailableQuantity() - quantity);
    }
}