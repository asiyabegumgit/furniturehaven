package org.perscholas.furniturehaven.service;

import lombok.extern.slf4j.Slf4j;
import org.perscholas.furniturehaven.model.Order;
import org.perscholas.furniturehaven.repository.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
@Slf4j
@Service
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    public List<Order> getAllOrders() {
        log.info("Fetching all orders");
        List<Order> orders = orderRepository.findAll();
        log.info("Retrieved {} orders", orders.size());
        return orders;
    }

    public Optional<Order> getOrderById(Long id) {
        log.info("Fetching order with ID {}", id);
        Optional<Order> order = orderRepository.findById(id);
        if (order.isPresent()) {
            log.info("Order found: {}", order.get());
        } else {
            log.warn("Order with ID {} not found", id);
        }
        return order;
    }

    public Order saveOrder(Order order) {
        log.info("Saving order: {}", order);
        Order savedOrder = orderRepository.save(order);
        log.info("Order saved with ID {}", savedOrder.getOrderId());
        return savedOrder;
    }
}