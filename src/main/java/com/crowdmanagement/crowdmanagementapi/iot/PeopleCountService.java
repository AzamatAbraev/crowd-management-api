package com.crowdmanagement.crowdmanagementapi.iot;

import lombok.Getter;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PeopleCountService {
    private final AtomicInteger currentCount = new AtomicInteger(0);
    private final ConcurrentHashMap<String, Integer> deviceCounts = new ConcurrentHashMap<>();


    @Getter
    private String lastDeviceName = "None";

    public void updateCount(int delta, String deviceName) {
        int newValue = currentCount.addAndGet(delta);
        this.lastDeviceName = deviceName;
        deviceCounts.merge(deviceName, delta, Integer::sum);

        if (newValue < 0) {
            currentCount.set(0);
        }
    }

    public int getCurrentCount() {
        return currentCount.get();
    }

    public void reset() {
        currentCount.set(0);
        this.lastDeviceName = "System Reset";
    }

    public Map<String, Integer> getDeviceCounts() {
        return new HashMap<>(deviceCounts);
    }
}
