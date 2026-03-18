package com.crowdmanagement.crowdmanagementapi.config;

import com.crowdmanagement.crowdmanagementapi.device.DeviceService;
import com.crowdmanagement.crowdmanagementapi.iot.InfluxDbTelemetryService;
import com.crowdmanagement.crowdmanagementapi.iot.PeopleCountService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.annotation.PreDestroy;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Configuration
public class MqttConfig {

    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);

    private final InfluxDbTelemetryService influxDbTelemetryService;


    @Value("${mqtt.broker-url}")
    private String brokerUrl;

    @Value("${mqtt.telemetry-topic}")
    private String telemetryTopic;

    @Value("${mqtt.status-topic}")
    private String statusTopic;

    @Value("${mqtt.qos}")
    private int qos;

    private IMqttClient mqttClient;

    private final PeopleCountService peopleCountService;
    private final DeviceService deviceService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MqttConfig(InfluxDbTelemetryService influxDbTelemetryService, PeopleCountService peopleCountService, DeviceService deviceService) {
        this.influxDbTelemetryService = influxDbTelemetryService;
        this.peopleCountService = peopleCountService;
        this.deviceService = deviceService;
    }

    @Bean
    public IMqttClient mqttClient() throws Exception {
        // Use a random suffix so multiple instances (e.g. dev + test) don't
        // conflict on the broker. Broker rejects duplicate client IDs.
        String clientId = "spring-backend-" + UUID.randomUUID().toString().substring(0, 8);

        mqttClient = new MqttClient(brokerUrl, clientId, new MemoryPersistence());

        MqttConnectOptions options = new MqttConnectOptions();
        options.setAutomaticReconnect(true);
        options.setCleanSession(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(20);

        logger.info("Connecting to MQTT broker at {}...", brokerUrl);
        mqttClient.connect(options);
        logger.info("MQTT connected. ClientId={}", clientId);

        mqttClient.subscribe(telemetryTopic, qos, (topic, message) -> {
            try {
                String raw = new String(message.getPayload(), StandardCharsets.UTF_8)
                        .trim().replace("\0", "");

                if (raw.isEmpty()) return;

                String[] parts = topic.split("/");
                String location = "Unknown";
                if (parts.length >= 7) {
                    location = parts[2] + " / " + parts[3] + " / " + parts[4];
                }

                logger.debug("MQTT TELEMETRY on [{}]: {}", topic, raw);

                JsonNode json = objectMapper.readTree(raw);

                int schemaVersion = json.has("v") ? json.get("v").asInt() : 0;
                if (schemaVersion > 1) {
                    logger.warn("Unknown payload schema version {} on {}. Skipping.", schemaVersion, topic);
                    return;
                }


                String deviceId;
                if (json.has("device_id")) {
                    deviceId = json.get("device_id").asText();
                } else if (json.has("device")) {
                    deviceId = json.get("device").asText();
                } else {
                    logger.warn("No device identifier in payload on {}: {}", topic, raw);
                    return;
                }

                if (json.has("seq")) {
                    int seq = json.get("seq").asInt();
                    int missedMessages = peopleCountService.checkAndRecordSeq(deviceId, seq);
                    if (missedMessages > 0) {
                        logger.warn("GAP DETECTED | device={} | missed {} messages | seq={}",
                                deviceId, missedMessages, seq);
                    }
                }


                JsonNode payloadNode = json.path("payload");
                if (payloadNode.path("misfire").asBoolean(false)) {
                    logger.info("MISFIRE ignored from device {}", deviceId);
                    deviceService.recordHeartbeat(deviceId, location);
                    return;
                }


                int delta;
                if (!payloadNode.isMissingNode() && payloadNode.has("count_delta")) {
                    delta = payloadNode.get("count_delta").asInt();
                } else if (json.has("count")) {
                    delta = json.get("count").asInt();
                } else {
                    logger.warn("No count field in payload from device {}", deviceId);
                    return;
                }

                JsonNode meta = json.path("meta");

                if (!meta.isMissingNode() && meta.has("rssi")) {
                    // RSSI could be stored if you add a column — for now just log it
                    int rssi = meta.get("rssi").asInt();
                    logger.debug("Device {} RSSI: {} dBm", deviceId, rssi);
                }

                if (!meta.isMissingNode() && meta.has("firmware")) {
                    String firmware = meta.get("firmware").asText();
                    deviceService.updateFirmwareVersion(deviceId, firmware);
                }

                peopleCountService.updateCount(delta, deviceId);

                deviceService.recordHeartbeat(deviceId, location);

                String buildingName = parts.length >= 3 ? parts[2] : "Unknown";
                String floorName = parts.length >= 4 ? parts[3] : "Unknown";
                String roomName = parts.length >= 5 ? parts[4] : "Unknown";

                influxDbTelemetryService.saveTelemetry(deviceId, buildingName, floorName, roomName, delta);

                if (json.has("seq")) {
                    logger.debug("Device {} seq={} delta={}", deviceId, json.get("seq").asInt(), delta);
                }

                logger.info("TELEMETRY | device={} | delta={} | totalCount={}",
                        deviceId, delta, peopleCountService.getCurrentCount());

            } catch (Exception e) {
                logger.error("Failed to process MQTT telemetry on {}: {}", topic, e.getMessage());
            }
        });

        mqttClient.subscribe(statusTopic, qos, (topic, message) -> {
            try {
                String raw = new String(message.getPayload(), StandardCharsets.UTF_8)
                        .trim().replace("\0", "");

                if (raw.isEmpty()) return;
                logger.debug("MQTT STATUS on [{}]: {}", topic, raw);

                String[] parts = topic.split("/");
                String location = "Unknown";
                if (parts.length >= 7) {
                    location = parts[2] + " / " + parts[3] + " / " + parts[4];
                }

                JsonNode json = objectMapper.readTree(raw);

                String deviceId = json.path("device_id").asText(null);
                String status   = json.path("status").asText(null);
                String type     = json.path("type").asText("unknown");  // "birth" or "death"

                if (deviceId == null || status == null) {
                    logger.warn("Malformed status message on {}", topic);
                    return;
                }

                if ("online".equals(status)) {
                    deviceService.markDeviceOnline(deviceId, location);
                    logger.info("DEVICE ONLINE | device={} location={} ({})", deviceId, location, type);

                } else if ("offline".equals(status)) {
                    String reason = json.path("reason").asText("unknown");
                    deviceService.markDeviceOffline(deviceId);
                    logger.warn("DEVICE OFFLINE | device={} | reason={} ({})", deviceId, reason, type);
                }

            } catch (Exception e) {
                logger.error("Failed to process MQTT status on {}: {}", topic, e.getMessage());
            }
        });

        logger.info("Subscribed to telemetry: {}", telemetryTopic);
        logger.info("Subscribed to status:    {}", statusTopic);

        return mqttClient;
    }

    @PreDestroy
    public void disconnect() {
        try {
            if (mqttClient != null && mqttClient.isConnected()) {
                mqttClient.disconnect();
                logger.info("MQTT client disconnected cleanly on shutdown");
            }
        } catch (Exception e) {
            logger.error("Error disconnecting MQTT client", e);
        }
    }
}
