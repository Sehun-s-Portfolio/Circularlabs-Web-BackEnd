package com.web.circularlabs_web_backend.inquiry.repository;

import com.web.circularlabs_web_backend.inquiry.domain.ClientInquiry;
import com.web.circularlabs_web_backend.inquiry.domain.Faq;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InquiryFaqRepository extends JpaRepository<Faq, Long> {
}
