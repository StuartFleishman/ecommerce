package com.ecommerce.cart.controller;

import com.ecommerce.cart.dto.CartProductDTO;
import com.ecommerce.cart.dto.CustomerCartDTO;
import com.ecommerce.cart.dto.ProductDTO;
import com.ecommerce.cart.service.CustomerCartService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Set;

@RequestMapping(value = "/cart-api")
@RestController
@CrossOrigin
@Validated
public class CustomerCartAPI {
    @Autowired
    private CustomerCartService customerCartService;

    @Autowired
    private Environment environment;

    @Autowired
    private RestTemplate template;

    Log logger = LogFactory.getLog(CustomerCartAPI.class);

    @PostMapping(value = "/products")
    public ResponseEntity<String> addProductToCart(@Valid @RequestBody CustomerCartDTO customerCartDTO)
            throws Exception {
        logger.info("Received a request to add products for " + customerCartDTO.getCustomerEmailId());
        Integer cartId = customerCartService.addProductToCart(customerCartDTO);
        String message = environment.getProperty("CustomerCartAPI.PRODUCT_ADDED_TO_CART");
        return new ResponseEntity<>(message + "  " + cartId, HttpStatus.CREATED);
    }

    @GetMapping(value = "/customer/{customerEmailId}/products")
    public ResponseEntity<Set<CartProductDTO>> getProductsFromCart(
            @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                    message = "{invalid.email.format}")
            @PathVariable("customerEmailId") String customerEmailId)
            throws Exception {
        logger.info("Received a request to get products details from the cart of "+customerEmailId);

        Set<CartProductDTO> cartProductDTOs = customerCartService.getProductsFromCart(customerEmailId);
        for (CartProductDTO cartProductDTO : cartProductDTOs) {
            ProductDTO productDTO = template.getForEntity("http://PRODUCTMS" + "/product-api/product/"
                            + cartProductDTO.getProduct().getProductId(), ProductDTO.class).getBody();
            cartProductDTO.setProduct(productDTO);
        }
        return new ResponseEntity<>(cartProductDTOs, HttpStatus.OK);

    }

    @DeleteMapping(value = "/customer/{customerEmailId}/product/{productId}")
    public ResponseEntity<String> deleteProductFromCart(
            @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                    message = "{invalid.email.format}")
            @PathVariable("customerEmailId") String customerEmailId,
            @NotNull(message = "{product.id.absent}") @PathVariable("productId") Integer productId)
            throws Exception {
        customerCartService.deleteProductFromCart(customerEmailId, productId);
        String message = environment.getProperty("CustomerCartAPI.PRODUCT_DELETED_FROM_CART_SUCCESS");
        return new ResponseEntity<>(message, HttpStatus.OK);

    }


    @PutMapping(value = "/customer/{customerEmailId}/product/{productId}")
    public ResponseEntity<String> modifyQuantityOfProductInCart(
            @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                    message = "{invalid.email.format}")
            @PathVariable("customerEmailId") String customerEmailId,
            @NotNull(message = "{product.id.absent}") @PathVariable("productId") Integer productId,
            @RequestBody Integer quantity) throws Exception {
        customerCartService.modifyQuantityOfProductInCart(customerEmailId, productId, quantity);
        String message = environment.getProperty("CustomerCartAPI.PRODUCT_QUANTITY_UPDATE_FROM_CART_SUCCESS");
        return new ResponseEntity<>(message, HttpStatus.OK);
    }

    @DeleteMapping(value = "/customer/{customerEmailId}/products")
    public ResponseEntity<String> deleteAllProductsFromCart(
            @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                    message = "{invalid.email.format}")
            @PathVariable("customerEmailId") String customerEmailId)
            throws Exception {
        logger.info("Received a request to clear the cart of "+customerEmailId );

        customerCartService.deleteAllProductsFromCart(customerEmailId);
        String message = environment.getProperty("CustomerCartAPI.ALL_PRODUCTS_DELETED");
        return new ResponseEntity<>(message, HttpStatus.OK);

    }

}