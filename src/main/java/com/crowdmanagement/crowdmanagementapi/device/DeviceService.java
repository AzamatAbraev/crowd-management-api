package com.crowdmanagement.crowdmanagementapi.device;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class DeviceService {
    private static final Logger logger = LoggerFactory.getLogger(DeviceService.class);
    private final DeviceRepository deviceRepository;

    public DeviceService(DeviceRepository deviceRepository) {
        this.deviceRepository = deviceRepository;
    }

    public Device registerDevice(Device device) {
        return deviceRepository.save(device);
    }

    public List<Device> getAllDevices(DeviceType type, DeviceStatus status) {
        if (type != null && status != null) {
            return deviceRepository.findByTypeAndStatus(type, status);
        } else if (type != null) {
            return deviceRepository.findByType(type);
        } else if (status != null) {
            return deviceRepository.findByStatus(status);
        }
        return deviceRepository.findAll();
    }

    public Optional<Device> getDeviceById(String id) {
        return deviceRepository.findById(id);
    }

    public Device updateDeviceDetails(String id, Device updatedDevice) {
        return deviceRepository.findById(id).map(device -> {
            if (updatedDevice.getName() != null) device.setName(updatedDevice.getName());
            if (updatedDevice.getLocation() != null) device.setLocation(updatedDevice.getLocation());
            if (updatedDevice.getType() != null) device.setType(updatedDevice.getType());
            if (updatedDevice.getSensors() != null) device.setSensors(updatedDevice.getSensors());
            if (updatedDevice.getFirmwareVersion() != null) device.setFirmwareVersion(updatedDevice.getFirmwareVersion());
            if (updatedDevice.getMetadata() != null) device.setMetadata(updatedDevice.getMetadata());
            return deviceRepository.save(device);
        }).orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));
    }

    public Device updateDeviceStatus(String id, DeviceStatus status, DeviceHealth health) {
        return deviceRepository.findById(id).map(device -> {
            if (status != null) device.setStatus(status);
            if (health != null) device.setHealth(health);
            return deviceRepository.save(device);
        }).orElseThrow(() -> new IllegalArgumentException("Device not found: " + id));
    }

    public void updateBatteryLevel(String id, Integer level) {
        deviceRepository.findById(id).ifPresent(device -> {
            device.setBatteryLevel(level);
            deviceRepository.save(device);
            logger.debug("Updated battery level for device {} to {}%", id, level);
        });
    }

    public void recordHeartbeat(String id) {
        deviceRepository.findById(id).ifPresentOrElse(device -> {
            device.setLastSeen(LocalDateTime.now());
            if (device.getStatus() == DeviceStatus.OFFLINE) {
                device.setStatus(DeviceStatus.ONLINE);
            }
            deviceRepository.save(device);
            logger.debug("Recorded heartbeat and set ONLINE for device {}", id);
        }, () -> {
            // Auto-provision basic entry if unknown device sends data via Mqtt
            logger.info("Auto-provisioning newly discovered device: {}", id);
            Device newDevice = Device.builder()
                .id(id)
                .name(id)
                .type(DeviceType.UNKNOWN)
                .status(DeviceStatus.ONLINE)
                .health(DeviceHealth.UNKNOWN)
                .lastSeen(LocalDateTime.now())
                .build();
            deviceRepository.save(newDevice);
        });
    }

    public void deleteDevice(String id) {
        deviceRepository.deleteById(id);
    }
}
