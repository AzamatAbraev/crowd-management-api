package com.crowdmanagement.crowdmanagementapi.iot;

import lombok.Getter;
import org.springframework.stereotype.Service;

@Getter
@Service
public class CountService {
    private volatile int currentCount = 0;

    public void updateCount(int count) {
        this.currentCount = count;
    }

}
