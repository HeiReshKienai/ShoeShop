package com.hutech.ShoeShop.controller;

import com.hutech.ShoeShop.model.CartItem;
import com.hutech.ShoeShop.model.Order;
import com.hutech.ShoeShop.service.CartService;
import com.hutech.ShoeShop.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("/order")
public class OrderController {
    @Autowired
    private OrderService orderService;
    @Autowired
    private CartService cartService;

    @GetMapping("/checkout")
    public String checkout() {
        return "/cart/checkout";
    }

    @PostMapping("/submit")
    public String submitOrder(String customerName, String phoneNumber, String address, String email, String note, String paymentMethod, String status, RedirectAttributes redirectAttributes) {
        List<CartItem> cartItems = cartService.getCartItems();
        if (cartItems.isEmpty()) {
            return "redirect:/cart"; // Redirect if cart is empty
        }
        Order order = orderService.createOrder(customerName, phoneNumber, address, email, note, paymentMethod, status, cartItems);
        redirectAttributes.addAttribute("orderId", order.getId());
        return "redirect:/order/confirmation";
    }

    @GetMapping("/confirmation")
    public String orderConfirmation(Long orderId, Model model) {
        Order order = orderService.getOrderById(orderId);
        if (order == null) {
            return "redirect:/cart";
        }
        model.addAttribute("order", order);
        model.addAttribute("orderDetails", order.getOrderDetails());
        model.addAttribute("totalAmount", orderService.calculateTotalAmount(order));
        return "cart/order-confirmation";
    }

    @GetMapping("/list")
    public String listOrders(Model model) {
        List<Order> orders = orderService.getAllOrders();
        model.addAttribute("orders", orders);
        model.addAttribute("orderService", orderService); // Add orderService to the model
        return "order/order-list";
    }
    @PostMapping("/update-status")
    public String updateOrderStatus(Long orderId, String newStatus, RedirectAttributes redirectAttributes) {
        Order updatedOrder = orderService.updateOrderStatus(orderId, newStatus);
        if (updatedOrder == null) {
            // Handle if order is not found
            return "redirect:/order/list";
        }
        redirectAttributes.addAttribute("orderId", orderId);
        return "redirect:/order/confirmation";
    }
}
