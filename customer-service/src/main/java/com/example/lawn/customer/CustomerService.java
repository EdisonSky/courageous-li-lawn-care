package com.example.lawn.customer;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.server.ResponseStatusException;

import java.time.Instant;

@Service
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    @Transactional
    public CustomerResponse create(CreateCustomerRequest request) {
        customerRepository.findByEmail(request.email()).ifPresent(c -> {
            throw new ResponseStatusException(HttpStatus.CONFLICT, "Email already registered");
        });
        Customer saved = customerRepository.save(toEntity(request));
        return toResponse(saved);
    }

    @Transactional(readOnly = true)
    public CustomerResponse getById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Customer not found"));
        return toResponse(customer);
    }

    private static Customer toEntity(CreateCustomerRequest r) {
        return Customer.builder()
                .fullName(r.fullName())
                .email(r.email())
                .phone(r.phone())
                .street(r.street())
                .city(r.city())
                .state(r.state())
                .zip(r.zip())
                .createdAt(Instant.now())
                .build();
    }

    private static CustomerResponse toResponse(Customer c) {
        return new CustomerResponse(
                c.getId(),
                c.getFullName(),
                c.getEmail(),
                c.getPhone(),
                c.getStreet(),
                c.getCity(),
                c.getState(),
                c.getZip(),
                c.getCreatedAt()
        );
    }
}
