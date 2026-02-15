package com.crowdmanagement.crowdmanagementapi.iot;

import org.springframework.context.annotation.Bean;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.messaging.MessageChannel;
import org.springframework.stereotype.Service;

@Service
public class MQTTMessageHandler {

    private final CountService countService;
    private final CountPublisher countPublisher;

    public MQTTMessageHandler(CountService countService,
                              CountPublisher countPublisher) {
        this.countService = countService;
        this.countPublisher = countPublisher;
    }

    @ServiceActivator(inputChannel = "mqttInputChannel")
    public void handleMessage(String payload) {
        int count = Integer.parseInt(payload);
        countService.updateCount(count);
        countPublisher.broadcast(count);
    }
}
