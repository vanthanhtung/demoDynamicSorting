package com.example.demo.service;

import com.example.demo.entity.Customer;
import com.example.demo.exception.CustomerException;
import com.example.demo.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.mapping.PropertyReferenceException;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CustomerService {

    @Autowired
    private CustomerRepository customerRepository;

    public Page<Customer> getAllCustomers(Integer page, Integer size, List<String> sortBy) {
        Page<Customer> pagedResult = null;
        try {
            List<Sort.Order> orderList = getOrders(sortBy);
            Sort sort = Sort.by(orderList);
            Pageable paging = PageRequest.of(page, size, sort);
            pagedResult = customerRepository.findAll(paging);
        } catch (PropertyReferenceException e) {
            throw new CustomerException("Field is not exist " + e.getPropertyName());
        }
        return pagedResult;
    }

    private Sort.Direction getSortDirection(String direction) {
        return ("desc".equals(direction)) ? Sort.Direction.DESC : Sort.Direction.ASC;
    }

    private List<Sort.Order> getOrders(List<String> sortBy) {
        return sortBy.stream().map(e -> {
            String[] input1 = e.split(",");
            String dir = (input1.length <=1) ? "" : input1[1];
            return new Sort.Order(getSortDirection(dir), input1[0]);
        }).collect(Collectors.toList());
    }

    public Page<Customer> getAllCustomers(Integer page, Integer size, String[] sortBy, String direction) {
        Page<Customer> pagedResult = null;
        try {
            Sort.Order[] orders = getOrders(sortBy);
            Sort sort = Sort.by(orders);
            Pageable paging = PageRequest.of(page, size, sort);
            pagedResult = customerRepository.findAll(paging);
        } catch (PropertyReferenceException e) {
            throw new CustomerException("Field is not exist " + e.getPropertyName());
        }
        return pagedResult;
    }

    private Sort.Order[] getOrders(String[] sortBy) {

        return Arrays.stream(sortBy).map(e -> {
            String[] input = e.split(",");
            return (input.length <= 1 || !input[1].equals("desc")) ?
                    Sort.Order.asc(input[0]) : Sort.Order.desc(input[0]);
        }).toArray(Sort.Order[]::new);
    }
}
