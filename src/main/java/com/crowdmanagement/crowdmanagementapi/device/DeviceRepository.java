package com.crowdmanagement.crowdmanagementapi.device;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DeviceRepository extends JpaRepository<Device, String> {
    List<Device> findByType(DeviceType type);
    List<Device> findByStatus(DeviceStatus status);
    List<Device> findByTypeAndStatus(DeviceType type, DeviceStatus status);
}
