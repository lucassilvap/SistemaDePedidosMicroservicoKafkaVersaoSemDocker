package com.example.servicodeinventario.core.repository;

import com.example.servicodeinventario.core.entity.Inventory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface InventoryRepository extends JpaRepository<Inventory, Long> {
        Optional<Inventory> findByProductCode(String productCode);
}
