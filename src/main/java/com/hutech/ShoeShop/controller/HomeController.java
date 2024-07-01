package com.hutech.ShoeShop.controller;

import com.hutech.ShoeShop.service.CategoryService;
import com.hutech.ShoeShop.service.ProductService;
import com.hutech.ShoeShop.service.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @Autowired
    private UserService userService;
    @Autowired
    private CategoryService categoryService;
    @Autowired
    private ProductService productService;

    @GetMapping("/")
    public String home(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String currentUsername = authentication.getName();
        model.addAttribute("currentUser", currentUsername);
        model.addAttribute("products", productService.getAllProducts());
        return "home/index";
    }

}