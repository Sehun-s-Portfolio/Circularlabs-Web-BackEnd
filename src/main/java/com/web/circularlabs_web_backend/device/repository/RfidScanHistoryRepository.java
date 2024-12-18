package com.web.circularlabs_web_backend.device.repository;

import com.web.circularlabs_web_backend.device.domain.RfidScanHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RfidScanHistoryRepository extends JpaRepository<RfidScanHistory, Long> {
}
