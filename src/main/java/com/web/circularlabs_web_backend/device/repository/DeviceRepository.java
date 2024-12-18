package com.web.circularlabs_web_backend.device.repository;

import com.web.circularlabs_web_backend.device.domain.Device;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DeviceRepository extends JpaRepository<Device, Long> {
}
