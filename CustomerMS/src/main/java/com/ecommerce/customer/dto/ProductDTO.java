
package com.ecommerce.customer.dto;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class ProductDTO {
	@NotNull(message = "{cartProduct.productId.absent}")
	private Integer productId;
	private String name;
	private String description;
	private String category;
	private String brand;
	private Double price;
	private Integer availableQuantity;
}