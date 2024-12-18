package com.web.circularlabs_web_backend.inquiry.repository;

import com.web.circularlabs_web_backend.inquiry.domain.ClientInquiry;
import com.web.circularlabs_web_backend.inquiry.domain.OrderInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface OrderInquiryRepository extends JpaRepository<OrderInquiry, Long> {
}
