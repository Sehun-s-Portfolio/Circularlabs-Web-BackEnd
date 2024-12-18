package com.web.circularlabs_web_backend.inquiry.repository;

import com.web.circularlabs_web_backend.device.domain.Device;
import com.web.circularlabs_web_backend.inquiry.domain.ClientInquiry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryRepository extends JpaRepository<ClientInquiry, Long> {
}
