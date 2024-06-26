package com.hutech.ShoeShop.service;

import com.hutech.ShoeShop.model.Manufacturer;
import com.hutech.ShoeShop.repository.ManufacturerRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@Transactional
public class ManufacturerService {
    private final ManufacturerRepository manufacturerRepository;

    public ManufacturerService(ManufacturerRepository manufacturerRepository) {
        this.manufacturerRepository = manufacturerRepository;
    }

    public List<Manufacturer> getAllManufacturers() {
        return manufacturerRepository.findAll();
    }

    public Optional<Manufacturer> getManufacturerById(Long id) {
        return manufacturerRepository.findById(id);
    }

    public Manufacturer addManufacturer(Manufacturer manufacturer) {
        return manufacturerRepository.save(manufacturer);
    }

    public Manufacturer updateManufacturer(Manufacturer manufacturer) {Manufacturer existingManufacturer = manufacturerRepository.findById(manufacturer.getId()).orElseThrow(() -> new IllegalStateException("Manufacturer with ID " + manufacturer.getId() + " does not exist."));
        existingManufacturer.setName(manufacturer.getName());
        existingManufacturer.setAddress(manufacturer.getAddress());
        // Set other properties here if needed
        return manufacturerRepository.save(existingManufacturer);
    }

    public void deleteManufacturerById(Long id) {
        if (!manufacturerRepository.existsById(id)) {
            throw new IllegalStateException("Manufacturer with ID " + id + " does not exist.");
        }
        manufacturerRepository.deleteById(id);
    }
}
