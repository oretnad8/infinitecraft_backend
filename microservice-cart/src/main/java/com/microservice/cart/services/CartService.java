package com.microservice.cart.services;

import com.microservice.cart.clients.ProductClient;
import com.microservice.cart.dto.CartItemResponse;
import com.microservice.cart.dto.ProductDto;
import com.microservice.cart.entities.Cart;
import com.microservice.cart.entities.CartItem;
import com.microservice.cart.entities.Order;
import com.microservice.cart.entities.OrderDetail;
import com.microservice.cart.repositories.CartRepository;
import com.microservice.cart.repositories.OrderRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class CartService {

    @Autowired
    private CartRepository cartRepository;

    @Autowired
    private OrderRepository orderRepository;

    @Autowired
    private ProductClient productClient;

    public Cart getCart(Long userId) {
        return cartRepository.findByUserId(userId).orElseGet(() -> {
            Cart cart = Cart.builder().userId(userId).build();
            return cartRepository.save(cart);
        });
    }

    @Transactional
    public Cart addToCart(Long userId, Long productId, Integer quantity) {
        Cart cart = getCart(userId);
        ProductDto product = productClient.findById(productId); // Validate product exists

        Optional<CartItem> existingItem = cart.getItems().stream()
                .filter(item -> item.getProductId().equals(productId))
                .findFirst();

        if (existingItem.isPresent()) {
            existingItem.get().setQuantity(existingItem.get().getQuantity() + quantity);
        } else {
            CartItem newItem = CartItem.builder()
                    .productId(productId)
                    .quantity(quantity)
                    .build();
            cart.addItem(newItem);
        }

        return cartRepository.save(cart);
    }

    @Transactional
    public Cart removeFromCart(Long userId, Long productId) {
        Cart cart = getCart(userId);
        cart.getItems().removeIf(item -> item.getProductId().equals(productId));
        return cartRepository.save(cart);
    }

    @Transactional
    public void clearCart(Long userId) {
        Cart cart = getCart(userId);
        cart.getItems().clear();
        cartRepository.save(cart);
    }

    @Transactional
    public Order checkout(Long userId) {
        Cart cart = getCart(userId);
        if (cart.getItems().isEmpty()) {
            throw new RuntimeException("Cart is empty");
        }

        Order order = Order.builder()
                .userId(userId)
                .date(LocalDateTime.now())
                .build();

        double total = 0;
        List<OrderDetail> details = new ArrayList<>();

        for (CartItem item : cart.getItems()) {
            ProductDto product = productClient.findById(item.getProductId());
            double itemTotal = product.getPrecio() * item.getQuantity();
            total += itemTotal;

            OrderDetail detail = OrderDetail.builder()
                    .order(order)
                    .productId(item.getProductId())
                    .quantity(item.getQuantity())
                    .price(product.getPrecio())
                    .build();
            details.add(detail);
        }

        order.setTotal(total);
        order.setDetails(details);

        Order savedOrder = orderRepository.save(order);

        // Clear cart after checkout
        cart.getItems().clear();
        cartRepository.save(cart);

        return savedOrder;
    }

    @Transactional
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new RuntimeException("Order not found"));
        order.setStatus(newStatus);
        return orderRepository.save(order);
    }

    public List<Order> getOrders(Long userId) {
        return orderRepository.findByUserId(userId);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }

    public List<CartItemResponse> getCartItems(Long userId) {
        Cart cart = getCart(userId);

        return cart.getItems().stream().map(item -> {
            ProductDto product = productClient.findById(item.getProductId());

            return CartItemResponse.builder()
                    .productId(product.getId())
                    .productName(product.getNombre())
                    .price(product.getPrecio())
                    .quantity(item.getQuantity())
                    .photo(product.getImagen()) // This will be the image URL or base64
                    .build();
        }).collect(Collectors.toList());
    }
}
