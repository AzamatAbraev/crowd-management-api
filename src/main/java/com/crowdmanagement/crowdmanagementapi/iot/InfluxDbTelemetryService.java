package com.crowdmanagement.crowdmanagementapi.iot;

import com.influxdb.v3.client.InfluxDBClient;
import com.influxdb.v3.client.Point;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Service
@EnableScheduling
public class InfluxDbTelemetryService {

    private final InfluxDBClient influxDBClient;

    public InfluxDbTelemetryService(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    private final List<Point> buffer = Collections.synchronizedList(new ArrayList<>());

    public void saveTelemetry(String deviceId, String building, String floor, String room, int count) {

        Point point = Point.measurement("occupancy")
                .setTag("deviceId", deviceId)
                .setTag("building", building)
                .setTag("floor", floor)
                .setTag("room", room)
                .setField("count", count)
                .setTimestamp(Instant.now());

        buffer.add(point);
    }

    @Scheduled(fixedRate = 10000)
    public void flushTelemetry() {
        if (buffer.isEmpty()) {
            return;
        }

        List<Point> pointsToWrite;
        synchronized (buffer) {
            pointsToWrite = new ArrayList<>(buffer);
            buffer.clear();
        }

        try {
            influxDBClient.writePoints(pointsToWrite);
            System.out.println("INFLUXDB | Batched write successful. Points: " + pointsToWrite.size());
        } catch (Exception e) {
            System.err.println("Failed to write batch to InfluxDB: " + e.getMessage());
        }
    }
}

