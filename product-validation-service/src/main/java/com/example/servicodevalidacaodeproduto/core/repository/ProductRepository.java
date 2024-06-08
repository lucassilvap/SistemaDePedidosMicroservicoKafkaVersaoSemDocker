package com.example.servicodevalidacaodeproduto.core.repository;

import com.example.servicodevalidacaodeproduto.core.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {

    Boolean existsByCode (String code);

}
