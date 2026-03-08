package com.crowdmanagement.crowdmanagementapi.config;

import com.crowdmanagement.crowdmanagementapi.device.DeviceService;
import com.crowdmanagement.crowdmanagementapi.iot.PeopleCountService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.IMqttClient;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.nio.charset.StandardCharsets;
import java.util.UUID;

@Configuration
public class MqttConfig {

    private static final String BROKER_URL = "tcp://172.26.164.80:1883"; // [type command "ip addr | grep inet" in case address changes]
    private static final String TOPIC = "building1/entrance/ultrasonic/#";

    private static final String CLIENT_ID = "spring-backend-" + UUID.randomUUID().toString().substring(0, 8);

    private final PeopleCountService peopleCountService;
    private final DeviceService deviceService;
    private final ObjectMapper objectMapper = new ObjectMapper();
    private static final Logger logger = LoggerFactory.getLogger(MqttConfig.class);

    public MqttConfig(PeopleCountService peopleCountService, DeviceService deviceService) {
        this.peopleCountService = peopleCountService;
        this.deviceService = deviceService;
    }

    @Bean
    public IMqttClient mqttClient() {
        try {
            IMqttClient client = new MqttClient(BROKER_URL, CLIENT_ID, new MemoryPersistence());

            MqttConnectOptions options = new MqttConnectOptions();
            options.setAutomaticReconnect(true);
            options.setCleanSession(true);
            options.setConnectionTimeout(10);
            options.setKeepAliveInterval(20);

            logger.info("Connecting to MQTT broker at {}...", BROKER_URL);
            client.connect(options);
            logger.info("Connected successfully to MQTT!");

            client.subscribe(TOPIC, 1, (topic, message) -> {
                try {
                    String payload = new String(message.getPayload(), StandardCharsets.UTF_8).trim().replace("\0", "");

                    if (payload.isEmpty()) return;

                    logger.debug("RAW PAYLOAD on {}: {}", topic, payload);

                    JsonNode json = objectMapper.readTree(payload);

                    if (json.has("count") && json.has("device")) {
                        int delta = json.get("count").asInt();
                        String device = json.get("device").asText();

                        // 1. Update domain specific crowd count
                        peopleCountService.updateCount(delta, device);

                        // 2. Manage IoT device lifecycle (heartbeat)
                        deviceService.recordHeartbeat(device);
                        
                        // Optional: if the hardware team ever adds "battery": 90 to the JSON payload:
                        if (json.has("battery")) {
                            deviceService.updateBatteryLevel(device, json.get("battery").asInt());
                        }

                        logger.info("SUCCESS | Device: {} | Delta: {} | Total: {}",
                                device, delta, peopleCountService.getCurrentCount());
                    } else {
                        logger.warn("Received JSON without count/device on {}: {}", topic, payload);
                    }

                } catch (Exception e) {
                    logger.error("FAILED to process message on {}: {}", topic, e.getMessage());
                }
            });

            logger.info("Subscribed to wildcard topic: {}", TOPIC);

            return client;

        } catch (Exception e) {
            logger.error("CRITICAL ERROR initializing MQTT Client", e);
            throw new RuntimeException("Failed to start MQTT", e);
        }
    }
}
