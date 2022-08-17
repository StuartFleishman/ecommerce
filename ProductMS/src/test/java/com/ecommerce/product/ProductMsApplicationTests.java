package com.ecommerce.product;

import java.lang.StackWalker.Option;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.juint.jupiter.api.BeforeEach;
import org.juint.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.boot.test.context.SpringBootTest;

import com.ecommerce.product.entity.Product;
import com.ecommerce.product.exception.EKartProductException;
import com.ecommerce.product.repository.ProductRepository;
import com.ecommerce.product.service.ProductServiceImpl;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class ProductMsApplicationTests {
	@Mock 
	private ProductRepository productRepository;

	@InjectMocks
	private ProductServiceImpl productService = new ProductServiceImpl();

	private Product product = new Product();

	@BeforeEach
	public void beforeEachMethod() {
		product.setProductId(2);
		product.setPrice(20000.00);
		product.setName("Tahoe");
		product.setDescription("Full-Size SUV");
		product.setCategory("Car");
		product.setBrand("Chevy");
		product.setAvailableQuantity(25);
	}


	@Test
	public void assertEmptyProductListThrowsError() throws EkartProductException {
		List<Product> emptyProductList = new ArrayList<>();
		Mockito.when(productRepository.findAll()).thenReturn(emptyProductList);
		Exception e = Assertions.assertThrows(EkartProductException.class, () -> productService.getAllProducts());
		Assertions.assertEquals("ProductService.PRODUCTS_NOT_AVAILABLE", e.getMessage());
	}

	@Test
	public void getAllProductsNotNull() throws EkartProductException {
		List<Product> productList = new ArrayList<>();
		productList.add(product);
		Mockito.when(productRepository.findAll()).thenReturn(productList);
		Assertions.assertNotNull(productService.getAllProducts());
	}

	@Test
	public void assertProductWithNoIdThrowsError() throws EkartProductException {
		product.setProductId(null);
		Mockito.when(productRepository.findById(1)).thenReturn(Optional.of(product));
		Exception e = Assertions.assertThrows(EkartProductException.class, () -> productService.getProductById(product.getProductId()));
		Assertions.assertEquals("ProductService.PRODUCT_NOT_AVAILABLE", e.getMessage());
	}

	@Test
	public void assertProductWithIdNotNull() throws EkartProductException {
		Mockito.when(productRepository.findById(2)).thenReturn(Optional.of(product));
		Assertions.assertNotNull(productService.getProductById(product.getProductId()));
	}

	@Test
	public void assertEqualsreduceAvailableQuantity() throws EkartProductException {
		Mockito.when(productRepository.findById(2)).thenReturn(Optional.of(product));
		Integer quanity = 10;
		productService.reduceAvaialableQuantity(product.getProductId(), quantity);
		Assertions.assertEquals(15, product.getAvailableQuantity());
	}

}