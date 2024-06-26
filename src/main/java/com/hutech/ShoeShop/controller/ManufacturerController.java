package com.hutech.ShoeShop.controller;

import com.hutech.ShoeShop.model.Manufacturer;
import com.hutech.ShoeShop.service.ManufacturerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import java.util.List;

@Controller
@RequiredArgsConstructor
public class ManufacturerController {
    private final ManufacturerService manufacturerService;

    @GetMapping("/manufacturers")
    public String listManufacturers(Model model) {
        List<Manufacturer> manufacturers = manufacturerService.getAllManufacturers();
        model.addAttribute("manufacturers", manufacturers);
        return "manufacturers/manufacturers-list";
    }

    @GetMapping("/manufacturers/add")
    public String showAddForm(Model model) {
        model.addAttribute("manufacturer", new Manufacturer());
        return "manufacturers/add-manufacturer";
    }

    @PostMapping("/manufacturers/add")
    public String addManufacturer(@Valid Manufacturer manufacturer, BindingResult result) {
        if (result.hasErrors()) {
            return "manufacturers/add-manufacturer";
        }
        manufacturerService.addManufacturer(manufacturer);
        return "redirect:/manufacturers";
    }

    @GetMapping("/manufacturers/edit/{id}")
    public String showUpdateForm(@PathVariable("id") Long id, Model model) {
        Manufacturer manufacturer = manufacturerService.getManufacturerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid manufacturer Id:" + id));
        model.addAttribute("manufacturer", manufacturer);
        return "manufacturers/update-manufacturer";
    }

    @PostMapping("/manufacturers/update/{id}")
    public String updateManufacturer(@PathVariable("id") Long id, @Valid Manufacturer manufacturer,
                                     BindingResult result, Model model) {
        if (result.hasErrors()) {
            manufacturer.setId(id);
            return "manufacturers/update-manufacturer";
        }
        manufacturerService.updateManufacturer(manufacturer);
        return "redirect:/manufacturers";
    }

    @GetMapping("/manufacturers/delete/{id}")
    public String deleteManufacturer(@PathVariable("id") Long id, Model model) {
        Manufacturer manufacturer = manufacturerService.getManufacturerById(id)
                .orElseThrow(() -> new IllegalArgumentException("Invalid manufacturer Id:" + id));
        manufacturerService.deleteManufacturerById(id);
        return "redirect:/manufacturers";
    }
}
