package com.ecommerce.customer.dto;

import lombok.Data;

@Data
public class OrderedProductDTO {
	private Integer orderedProductId;
	private ProductDTO product;
	private Integer quantity;
}