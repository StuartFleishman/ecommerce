package com.ecommerce.customer.controller;

import com.ekart.customer.dto.*;
import com.ekart.customer.service.OrderService;
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
import java.util.ArrayList;
import java.util.List;

@RequestMapping(value = "/customerorder-api")
@RestController
@Validated
@CrossOrigin
public class OrderAPI {

    @Autowired
    private OrderService orderService;

    @Autowired
    private Environment environment;
    @Autowired
    RestTemplate template;



    @PostMapping(value = "/place-order")
    public ResponseEntity<String> placeOrder(@Valid @RequestBody OrderDTO order) throws Exception {
    

        ResponseEntity<CartProductDTO[]> cartProductDTOsResponse = template.getForEntity(
                "http://CARTMS" + "/cart-api/customer/" + order.getCustomerEmailId() + "/products",
                CartProductDTO[].class);

    

        CartProductDTO[] cartProductDTOs = cartProductDTOsResponse.getBody();
        template.delete("http://CARTMS" + "/cart-api/customer/" + order.getCustomerEmailId() + "/products");

    

        List<OrderedProductDTO> orderedProductDTOs = new ArrayList<>();

        assert cartProductDTOs != null;
        for (CartProductDTO cartProductDTO : cartProductDTOs) {
            OrderedProductDTO orderedProductDTO = new OrderedProductDTO();
            orderedProductDTO.setProduct(cartProductDTO.getProduct());
            orderedProductDTO.setQuantity(cartProductDTO.getQuantity());
            orderedProductDTOs.add(orderedProductDTO);
        }
        order.setOrderedProducts(orderedProductDTOs);

        Integer orderId = orderService.placeOrder(order);
        String modificationSuccessMsg = environment.getProperty("OrderAPI.ORDER_PLACED_SUCCESSFULLY");

        return new ResponseEntity<>(modificationSuccessMsg + orderId, HttpStatus.CREATED);
    }



    @GetMapping(value = "/order/{orderId}")
    public ResponseEntity<OrderDTO> getOrderDetails(
            @NotNull(message = "{orderId.absent}") @PathVariable Integer orderId) throws Exception {
        OrderDTO orderDTO = orderService.getOrderDetails(orderId);
        for (OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {

            ResponseEntity<ProductDTO> productResponse = template.getForEntity(
                    "http://PRODUCTMS" + "/product-api/product/" + orderedProductDTO.getProduct().getProductId(),
                    ProductDTO.class);

            orderedProductDTO.setProduct(productResponse.getBody());
        }
        return new ResponseEntity<>(orderDTO, HttpStatus.OK);
    }

    @GetMapping(value = "customer/{customerEmailId}/orders")
    public ResponseEntity<List<OrderDTO>> getOrdersOfCustomer(
            @Pattern(regexp = "[a-zA-Z0-9._]+@[a-zA-Z]{2,}\\.[a-zA-Z][a-zA-Z.]+",
                    message = "{invalid.email.format}")
            @PathVariable String customerEmailId)
            throws Exception {
        List<OrderDTO> orderDTOs = orderService.findOrdersByCustomerEmailId(customerEmailId);
        for (OrderDTO orderDTO : orderDTOs) {
            for (OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {

                ResponseEntity<ProductDTO> productResponse = template.getForEntity(
                        "http://PRODUCTMS" + "/product-api/product/" + orderedProductDTO.getProduct().getProductId(),
                        ProductDTO.class);
                orderedProductDTO.setProduct(productResponse.getBody());
            }
        }
        return new ResponseEntity<>(orderDTOs, HttpStatus.OK);
    }

    @PutMapping(value = "order/{orderId}/update/order-status")
    public void updateOrderAfterPayment(@NotNull(message = "{orderId.absent}") @PathVariable Integer orderId,
                                        @RequestBody String transactionStatus) throws Exception {
        if (transactionStatus.equals("TRANSACTION_SUCCESS")) {
            orderService.updateOrderStatus(orderId, OrderStatus.CONFIRMED);
            OrderDTO orderDTO = orderService.getOrderDetails(orderId);
            for (OrderedProductDTO orderedProductDTO : orderDTO.getOrderedProducts()) {

                template.put(
                        "http://PRODUCTMS" + "/product-api/update/" + orderedProductDTO.getProduct().getProductId(),
                        orderedProductDTO.getQuantity());
            }
        } else {
            orderService.updateOrderStatus(orderId, OrderStatus.CANCELLED);
        }
    }

    @PutMapping(value = "order/{orderId}/update/payment-through")
    public void updatePaymentOption(@NotNull(message = "{orderId.absent}") @PathVariable Integer orderId,
                                    @RequestBody String paymentThrough) throws Exception {
        if (paymentThrough.equalsIgnoreCase("DEBIT_CARD")) {
            orderService.updatePaymentThrough(orderId, PaymentThrough.DEBIT_CARD);
        } else {
            orderService.updatePaymentThrough(orderId, PaymentThrough.CREDIT_CARD);
        }
    }

}