package com.hutech.ShoeShop.controller;

import com.hutech.ShoeShop.model.Category;
import com.hutech.ShoeShop.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/categories")
public class CategoryApiController {
    @Autowired
    private final CategoryService categoryService;
    @GetMapping
    public List<Category> getCategories() {
        return categoryService.getAllCategories();
    }
    @GetMapping("/{id}")
    public Optional<Category> getCategory(@PathVariable Long id) {
        return categoryService.getCategoryById(id);
    }
    @PostMapping
    public void createCategory(@RequestBody Category category) {
        categoryService.addCategory(category);
    }
    @PutMapping("/{id}")
    public void updateCategory(@PathVariable Long id, @RequestBody Category category) {
        category.setId(id);
        category.setName(category.getName());
        categoryService.updateCategory(category);
    }
}
