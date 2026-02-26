package com.crowdmanagement.crowdmanagementapi.iot;

import org.springframework.stereotype.Service;
import java.util.concurrent.atomic.AtomicInteger;

@Service
public class PeopleCountService {
    private final AtomicInteger currentCount = new AtomicInteger(0);

    public void updateCount(int delta) {
        int newValue = currentCount.addAndGet(delta);
        if (newValue < 0) {
            currentCount.set(0);
        }
    }

    public int getCurrentCount() {
        return currentCount.get();
    }
}
