package com.crowdmanagement.crowdmanagementapi.iot;

import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class CountPublisher {

    private final SimpMessagingTemplate messagingTemplate;

    public CountPublisher(SimpMessagingTemplate messagingTemplate) {
        this.messagingTemplate = messagingTemplate;
    }

    public void broadcast(int count) {
        messagingTemplate.convertAndSend("/topic/count", count);
    }
}
