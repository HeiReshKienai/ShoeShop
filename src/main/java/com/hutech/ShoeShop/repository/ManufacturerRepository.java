package com.hutech.ShoeShop.repository;

import com.hutech.ShoeShop.model.Manufacturer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
@Repository
public interface ManufacturerRepository extends JpaRepository<Manufacturer, Long> {
}