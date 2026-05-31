package com.example.lawn.signup;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@FeignClient(name = "customer-service", url = "${app.customer-service.url}")
public interface CustomerClient {

    @PostMapping("/api/customers")
    CustomerResponse createCustomer(@RequestBody CreateCustomerRequest request);
}
