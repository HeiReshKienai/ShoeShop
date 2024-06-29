package com.hutech.ShoeShop.controller;

import com.hutech.ShoeShop.model.User;
import com.hutech.ShoeShop.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
public class UserApiController {
    private final UserService userService;
    @GetMapping
    public List<User> getUsers() {
        return userService.findAll();
    }
    @GetMapping("/{username}")
    public Optional<User> getUser(@PathVariable String username) {
        return userService.findByUsername(username);
    }
    @PostMapping
    public void createUser(@RequestBody User user) {
        userService.save(user);
    }
    @PutMapping("/{username}")
    public void updateUser(@PathVariable String username, @RequestBody User user) {
        user.setUsername(username);
        user.setAddress(user.getAddress());
        user.setEmail(user.getEmail());
        user.setPhone(user.getPhone());
        user.setFullName(user.getFullName());
        userService.updateUserByUsername(username, user);
    }
}
