package com.crowdmanagement.crowdmanagementapi.device;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/devices")
public class DeviceController {

    private final DeviceService deviceService;

    public DeviceController(DeviceService deviceService) {
        this.deviceService = deviceService;
    }

    @PostMapping
    public ResponseEntity<Device> registerDevice(@RequestBody Device device) {
        Device savedDevice = deviceService.registerDevice(device);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedDevice);
    }

    @GetMapping
    public ResponseEntity<List<Device>> getAllDevices(
            @RequestParam(required = false) DeviceType type,
            @RequestParam(required = false) DeviceStatus status) {
        List<Device> devices = deviceService.getAllDevices(type, status);
        return ResponseEntity.ok(devices);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> getDeviceById(@PathVariable String id) {
        return deviceService.getDeviceById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> updateDeviceDetails(@PathVariable String id, @RequestBody Device device) {
        try {
            Device updatedDevice = deviceService.updateDeviceDetails(id, device);
            return ResponseEntity.ok(updatedDevice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.notFound().build();
        }
    }

    @PatchMapping("/{id}/status")
    public ResponseEntity<Device> updateDeviceStatus(
            @PathVariable String id,
            @RequestBody Map<String, String> statusUpdate) {
        try {
            DeviceStatus newStatus = statusUpdate.containsKey("status") ? DeviceStatus.valueOf(statusUpdate.get("status")) : null;
            DeviceHealth newHealth = statusUpdate.containsKey("health") ? DeviceHealth.valueOf(statusUpdate.get("health")) : null;

            Device updatedDevice = deviceService.updateDeviceStatus(id, newStatus, newHealth);
            return ResponseEntity.ok(updatedDevice);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build();
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDevice(@PathVariable String id) {
        deviceService.deleteDevice(id);
        return ResponseEntity.noContent().build();
    }
}
