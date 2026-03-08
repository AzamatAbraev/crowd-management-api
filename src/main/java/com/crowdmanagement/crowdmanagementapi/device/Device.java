package com.crowdmanagement.crowdmanagementapi.device;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Entity
@Table(name = "devices")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Device {

    @Id
    @Column(nullable = false, unique = true)
    private String id; // MAC Address or unique serial number

    @Column(nullable = false)
    private String name;

    private String location;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceType type;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceStatus status = DeviceStatus.OFFLINE;

    @Builder.Default
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private DeviceHealth health = DeviceHealth.UNKNOWN;

    private Integer batteryLevel; // Percentage, can be null for wired devices
    
    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "TEXT")
    private List<String> sensors;

    private String firmwareVersion;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime lastSeen;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    @Column(updatable = false)
    private LocalDateTime registeredAt;

    @Convert(converter = MapConverter.class)
    @Column(columnDefinition = "TEXT")
    private Map<String, Object> metadata;

    @PrePersist
    protected void onCreate() {
        if (registeredAt == null) {
            registeredAt = LocalDateTime.now();
        }
    }
}

@Converter
class StringListConverter implements AttributeConverter<List<String>, String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(List<String> attribute) {
        try {
            return attribute != null ? mapper.writeValueAsString(attribute) : null;
        } catch (JsonProcessingException e) {
            return "[]";
        }
    }

    @Override
    public List<String> convertToEntityAttribute(String dbData) {
        try {
            return dbData != null ? mapper.readValue(dbData, new TypeReference<>() {}) : List.of();
        } catch (JsonProcessingException e) {
            return List.of();
        }
    }
}

@Converter
class MapConverter implements AttributeConverter<Map<String, Object>, String> {
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    public String convertToDatabaseColumn(Map<String, Object> attribute) {
        try {
            return attribute != null ? mapper.writeValueAsString(attribute) : null;
        } catch (JsonProcessingException e) {
            return "{}";
        }
    }

    @Override
    public Map<String, Object> convertToEntityAttribute(String dbData) {
        try {
            return dbData != null ? mapper.readValue(dbData, new TypeReference<>() {}) : new HashMap<>();
        } catch (JsonProcessingException e) {
            return new HashMap<>();
        }
    }
}
