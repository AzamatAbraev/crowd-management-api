package com.crowdmanagement.crowdmanagementapi.analytics;

import com.influxdb.v3.client.InfluxDBClient;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Service
public class AnalyticsService {

    private final InfluxDBClient influxDBClient;

    public AnalyticsService(InfluxDBClient influxDBClient) {
        this.influxDBClient = influxDBClient;
    }

    private List<AnalyticsResponse> executeParametrizedQuery(String sql, Map<String, Object> params) {
        try (Stream<Object[]> stream = influxDBClient.query(sql, params)) {
            return stream.map(row -> {
//                row[0] = time_bucket (from date_bin)
//                row[1] = avg_count (from AVG(count))
                String label = row[0] != null ? row[0].toString() : "N/A";

                Double value = 0.0;
                if (row[1] instanceof Number n) {
                    value = n.doubleValue();
                }

                return new AnalyticsResponse(label, value);
            }).collect(Collectors.toList());
        } catch (Exception e) {
            // Log error for your internship project debugging
            System.err.println("Error querying InfluxDB: " + e.getMessage());
            return List.of();
        }
    }

    // --- BUILDING ANALYTICS ---

    public List<AnalyticsResponse> getBuildingToday(String building) {
        String sql = """
            SELECT date_bin(interval '1 hour', time) as time_bucket, AVG(count) as avg_count
            FROM "occupancy"
            WHERE building = $building AND time >= date_trunc('day', now())
            GROUP BY time_bucket ORDER BY time_bucket ASC
            """;
        return executeParametrizedQuery(sql, Map.of("building", building));
    }

    public List<AnalyticsResponse> getBuildingWeek(String building) {
        String sql = """
            SELECT date_bin(interval '1 day', time) as time_bucket, AVG(count) as avg_count
            FROM "occupancy"
            WHERE building = $building AND time >= date_trunc('week', now())
            GROUP BY time_bucket ORDER BY time_bucket ASC
            """;
        return executeParametrizedQuery(sql, Map.of("building", building));
    }

    public List<AnalyticsResponse> getBuildingYear(String building) {
        String sql = """
            SELECT date_bin(interval '1 month', time) as time_bucket, AVG(count) as avg_count
            FROM "occupancy"
            WHERE building = $building AND time >= date_trunc('year', now())
            GROUP BY time_bucket ORDER BY time_bucket ASC
            """;
        return executeParametrizedQuery(sql, Map.of("building", building));
    }

    // --- ROOM ANALYTICS ---

    public List<AnalyticsResponse> getRoomToday(String room) {
        String sql = """
            SELECT date_bin(interval '1 hour', time) as time_bucket, AVG(count) as avg_count
            FROM "occupancy"
            WHERE room = $room AND time >= date_trunc('day', now())
            GROUP BY time_bucket ORDER BY time_bucket ASC
            """;
        return executeParametrizedQuery(sql, Map.of("room", room));
    }

    public List<AnalyticsResponse> getRoomWeek(String room) {
        String sql = """
            SELECT date_bin(interval '1 day', time) as time_bucket, AVG(count) as avg_count
            FROM "occupancy"
            WHERE room = $room AND time >= date_trunc('week', now())
            GROUP BY time_bucket ORDER BY time_bucket ASC
            """;
        return executeParametrizedQuery(sql, Map.of("room", room));
    }

    public List<AnalyticsResponse> getRoomYear(String room) {
        String sql = """
            SELECT date_bin(interval '1 month', time) as time_bucket, AVG(count) as avg_count
            FROM "occupancy"
            WHERE room = $room AND time >= date_trunc('year', now())
            GROUP BY time_bucket ORDER BY time_bucket ASC
            """;
        return executeParametrizedQuery(sql, Map.of("room", room));
    }
}