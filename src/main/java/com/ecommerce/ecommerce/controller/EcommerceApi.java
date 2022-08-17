package com.ecommerce.ecommerce.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path="api")
public class EcommerceApi {
  
  @GetMapping
  public String getStatus() {
    return "hello world";
  }

}
