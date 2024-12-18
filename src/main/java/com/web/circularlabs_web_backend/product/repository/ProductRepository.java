package com.web.circularlabs_web_backend.product.repository;

import com.web.circularlabs_web_backend.product.domain.Product;
import com.web.circularlabs_web_backend.product.domain.SupplyProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
}
