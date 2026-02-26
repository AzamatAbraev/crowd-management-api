package com.crowdmanagement.crowdmanagementapi.config;

import com.crowdmanagement.crowdmanagementapi.iot.PeopleCountService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.config.EnableIntegration;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.core.MqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;

import java.nio.charset.StandardCharsets;

@Configuration
@EnableIntegration
public class MqttConfig {

    private static final String BROKER_URL = "tcp://172.26.164.80:1883";
    private static final String TOPIC = "building1/entrance/ultrasonic/count";
    private static final String CLIENT_ID = "spring-backend-counter";

    private final PeopleCountService peopleCountService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    public MqttConfig(PeopleCountService peopleCountService) {
        this.peopleCountService = peopleCountService;
    }

    /* -------------------------------------------------
       MQTT CONNECTION
    ------------------------------------------------- */

    @Bean
    public MqttPahoClientFactory mqttClientFactory() {

        DefaultMqttPahoClientFactory factory =
                new DefaultMqttPahoClientFactory();

        MqttConnectOptions options = new MqttConnectOptions();

        options.setServerURIs(new String[]{BROKER_URL});
        options.setCleanSession(true);
        options.setAutomaticReconnect(true);
        options.setConnectionTimeout(10);
        options.setKeepAliveInterval(20);

        factory.setConnectionOptions(options);

        return factory;
    }

    /* -------------------------------------------------
       INTERNAL CHANNEL
    ------------------------------------------------- */

    @Bean(name = "mqttInputChannel")
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /* -------------------------------------------------
       MQTT LISTENER (FORCED STARTUP)
    ------------------------------------------------- */

    @Bean
    public MessageProducer mqttInbound() {

        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(
                        CLIENT_ID,
                        mqttClientFactory(),
                        TOPIC
                );

        adapter.setCompletionTimeout(5000);
        adapter.setQos(1);
        adapter.setAutoStartup(true);          // 🔴 CRITICAL
        adapter.setOutputChannel(mqttInputChannel());

        DefaultPahoMessageConverter converter =
                new DefaultPahoMessageConverter();

        converter.setPayloadAsBytes(true);     // 🔴 FORCE byte[]
        adapter.setConverter(converter);

        System.out.println("MQTT Adapter initialized for topic: " + TOPIC);

        return adapter;
    }

    /* -------------------------------------------------
       MESSAGE PROCESSOR
    ------------------------------------------------- */

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler mqttHandler() {

        return message -> {

            try {
                /* ---------- Decode Payload ---------- */

                byte[] payloadBytes = (byte[]) message.getPayload();

                String payload = new String(
                        payloadBytes,
                        StandardCharsets.UTF_8
                );

                System.out.println("MQTT RECEIVED: " + payload);

                /* ---------- Parse JSON ---------- */

                JsonNode json = objectMapper.readTree(payload);

                int delta = json.get("count").asInt();
                String device = json.get("device").asText();

                /* ---------- Update Counter ---------- */

                peopleCountService.updateCount(delta);

                int total = peopleCountService.getCurrentCount();

                /* ---------- Log ---------- */

                System.out.println(
                        "[MQTT] Device=" + device +
                                " Delta=" + delta +
                                " Total=" + total
                );

            } catch (Exception e) {

                System.err.println("MQTT PROCESSING FAILED");
                e.printStackTrace();
            }
        };
    }
}