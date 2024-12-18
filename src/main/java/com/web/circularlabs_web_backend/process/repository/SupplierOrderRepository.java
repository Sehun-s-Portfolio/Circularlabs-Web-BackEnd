package com.web.circularlabs_web_backend.process.repository;

import com.web.circularlabs_web_backend.process.domain.SupplierOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SupplierOrderRepository extends JpaRepository<SupplierOrder, Long> {
}
