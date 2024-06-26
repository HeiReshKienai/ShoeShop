package com.hutech.ShoeShop.service;
import com.hutech.ShoeShop.model.CartItem;
import com.hutech.ShoeShop.model.Order;
import com.hutech.ShoeShop.model.OrderDetail;
import com.hutech.ShoeShop.repository.OrderDetailRepository;
import com.hutech.ShoeShop.repository.OrderRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderService {
    @Autowired
    private OrderRepository orderRepository;
    @Autowired
    private OrderDetailRepository orderDetailRepository;
    @Autowired
    private CartService cartService;

    @Transactional
    public Order createOrder(String customerName, String phoneNumber, String address, String email, String note, String paymentMethod, String status, List<CartItem> cartItems) {
        Order order = new Order();
        order.setCustomerName(customerName);
        order.setPhoneNumber(phoneNumber);
        order.setAddress(address);
        order.setPaymentDateTime(LocalDateTime.now());
        order.setEmail(email);
        order.setNote(note);
        order.setPaymentMethod(paymentMethod);
        order.setStatus("đặt hàng thành công");

        order = orderRepository.save(order);
        for (CartItem item : cartItems) {
            OrderDetail detail = new OrderDetail();
            detail.setOrder(order);
            detail.setProduct(item.getProduct());
            detail.setQuantity(item.getQuantity());

            orderDetailRepository.save(detail);
        }
        cartService.clearCart();
        return order;
    }

    public double calculateTotalAmount(Order order) {
        return order.getOrderDetails().stream()
                .mapToDouble(detail -> detail.getProduct().getPrice() * detail.getQuantity())
                .sum();
    }

    public Order getOrderById(Long id) {
        Optional<Order> order = orderRepository.findById(id);
        return order.orElse(null);
    }

    public List<Order> getAllOrders() {
        return orderRepository.findAll();
    }
    public Order updateOrderStatus(Long orderId, String newStatus) {
        Optional<Order> optionalOrder = orderRepository.findById(orderId);
        if (optionalOrder.isPresent()) {
            Order order = optionalOrder.get();
            order.setStatus(newStatus);
            return orderRepository.save(order);
        }
        return null; // or throw an exception if order is not found
    }



}
