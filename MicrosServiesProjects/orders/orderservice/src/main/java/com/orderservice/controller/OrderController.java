package com.orderservice.controller;

import com.orderservice.dto.OrderResponseDTO;
import com.orderservice.dto.ProductDTO;
import com.orderservice.entity.Orders;
import com.orderservice.repository.OrdersRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.List;

@RestController
@RequestMapping("/orders")
public class OrderController {

    @Autowired
    private OrdersRepository ordersRepository;

    @Autowired
    private WebClient.Builder webClientBuilder;

    @PostMapping("/placeorder")
    public Mono<ResponseEntity<OrderResponseDTO>> placeOrder(@RequestBody Orders order) {
        // Validate input
        if (order == null || order.getProductId() == null || order.getQuantity() == null) {
            return Mono.just(ResponseEntity.badRequest().body(null));
        }

        return webClientBuilder.build()
                .get()
                .uri("http://localhost:8081/products/" + order.getProductId())
                .retrieve()
                .bodyToMono(ProductDTO.class)
                .map(productDTO -> {
                    // Save the order first to generate the ID
                    Orders savedOrder = ordersRepository.save(order);

                    // Create and populate the response DTO
                    OrderResponseDTO responseDTO = new OrderResponseDTO();
                    responseDTO.setOrderId(savedOrder.getId()); // ID is now generated
                    responseDTO.setProductId(savedOrder.getProductId());
                    responseDTO.setQuantity(savedOrder.getQuantity());
                    responseDTO.setProductName(savedOrder.getProductName());


                    responseDTO.setProductName(productDTO.getProductName());
                    responseDTO.setProductPrice(productDTO.getPrice());
                    responseDTO.setTotalPrice(savedOrder.getQuantity() * productDTO.getPrice());

                    return ResponseEntity.ok(responseDTO);
                });
    }


    @GetMapping("/all")
    public List<Orders> getAllOrders() {
        return ordersRepository.findAll();
    }
}
