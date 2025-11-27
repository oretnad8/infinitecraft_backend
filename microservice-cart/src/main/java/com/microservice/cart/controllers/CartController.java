package com.microservice.cart.controllers;

import com.microservice.cart.dto.AddToCartRequest;
import com.microservice.cart.dto.CartItemResponse;
import com.microservice.cart.entities.Cart;
import com.microservice.cart.entities.Order;
import com.microservice.cart.services.CartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartService cartService;

    @GetMapping("/{userId}")
    public ResponseEntity<Cart> getCart(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCart(userId));
    }

    @GetMapping("/{userId}/items")
    public ResponseEntity<List<CartItemResponse>> getCartItems(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getCartItems(userId));
    }

    @PostMapping("/{userId}/add")
    public ResponseEntity<Cart> addToCart(@PathVariable Long userId, @RequestBody AddToCartRequest request) {
        return ResponseEntity.ok(cartService.addToCart(userId, request.getProductId(), request.getQuantity()));
    }

    @DeleteMapping("/{userId}/remove/{productId}")
    public ResponseEntity<Cart> removeFromCart(@PathVariable Long userId, @PathVariable Long productId) {
        return ResponseEntity.ok(cartService.removeFromCart(userId, productId));
    }

    @DeleteMapping("/{userId}/clear")
    public ResponseEntity<Void> clearCart(@PathVariable Long userId) {
        cartService.clearCart(userId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/{userId}/checkout")
    public ResponseEntity<Order> checkout(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.checkout(userId));
    }

    @PutMapping("/orders/{orderId}/status")
    public ResponseEntity<Order> updateOrderStatus(@PathVariable Long orderId, @RequestBody Map<String, String> request) {
        String newStatus = request.get("status");
        return ResponseEntity.ok(cartService.updateOrderStatus(orderId, newStatus));
    }

    // Order endpoints
    @GetMapping("/orders/{userId}")
    public ResponseEntity<List<Order>> getOrders(@PathVariable Long userId) {
        return ResponseEntity.ok(cartService.getOrders(userId));
    }

    @GetMapping("/orders")
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(cartService.getAllOrders());
    }
}
