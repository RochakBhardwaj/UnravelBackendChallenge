package com.example.UnravelChallenge.UnravelBackendChallenge.part5DatabaseManager;

import com.zaxxer.hikari.HikariDataSource;
import com.zaxxer.hikari.HikariPoolMXBean;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

// Component Annotation Given Making it Singleton
@Slf4j
@Component
@RequiredArgsConstructor
public class DatabaseManager {

    private final HikariDataSource dataSource;

    private static final int MAX_SAMPLES = 50;
    private final Queue<Long> acquisitionTimes = new ConcurrentLinkedQueue<>();

    // thresholds
    private static final double HIGH_WAIT_THRESHOLD_MS = 1500;   // 1.5s avg wait
    private static final double LOW_WAIT_THRESHOLD_MS = 300;     // under 300ms
    private static final double HIGH_USAGE_RATIO = 0.85;         // 85% utilization
    private static final double LOW_USAGE_RATIO = 0.25;          // 25% utilization

    private static final int MAX_POOL_LIMIT = 150;
    private static final int MIN_POOL_LIMIT = 100;
    private static final int MAX_IDLE_LIMIT = 20;
    private static final int MIN_IDLE_LIMIT = 5;

    private volatile long lastScaleTime = 0;

    @PostConstruct
    public void initPool() {
        try (Connection conn = dataSource.getConnection()) {
            log.info("Hikari pool initialized successfully during @PostConstruct");
        } catch (SQLException e) {
            log.error("Failed to initialize Hikari pool during @PostConstruct", e);
        }
    }

    public Connection getConnection() throws SQLException {
        long start = System.currentTimeMillis();
        Connection conn = dataSource.getConnection();
        long duration = System.currentTimeMillis() - start;

        if (duration > 5000) {
            log.warn("Connection acquisition took too long: {} ms", duration);
        }

        acquisitionTimes.add(duration);
        if (acquisitionTimes.size() > MAX_SAMPLES) {
            acquisitionTimes.poll();
        }

        return conn;
    }

    public void closeConnection(Connection connection) {
        try {
            if (connection != null && !connection.isClosed()) {
                connection.close();
            }
        } catch (SQLException e) {
            log.error("Error closing connection", e);
        }
    }

//    60 Seconds Used for Scheduler to demonstrate working, in actual usage we can choose an optimal value

    @Scheduled(fixedRate = 60000)
    public void monitorPool() {
        HikariPoolMXBean poolBean = dataSource.getHikariPoolMXBean();

        log.info("Monitoring pool started");

        if (poolBean == null) {
            log.info("Pool not initialized yet - skipping monitoring");
            return;
        }

        int active = poolBean.getActiveConnections();
        int idle = poolBean.getIdleConnections();
        int total = poolBean.getTotalConnections();

        double avgWait = acquisitionTimes.stream()
                .mapToLong(Long::longValue)
                .average()
                .orElse(0.0);

        double usageRatio = total == 0 ? 0 : (double) active / total;

        log.info("Pool metrics: active={}, idle={}, total={}, usage={}%, avgWait={}ms",
                active, idle, total, Math.round(usageRatio * 100), (long) avgWait);

        int currentMax = dataSource.getMaximumPoolSize();
        int currentMin = dataSource.getMinimumIdle();

        log.info("" + avgWait + " ms");

        // === SCALE UP ===
        if (avgWait > HIGH_WAIT_THRESHOLD_MS && usageRatio > HIGH_USAGE_RATIO) {
            int newMax = Math.min(currentMax + 10, MAX_POOL_LIMIT);
            int newMin = Math.min(currentMin + 2, MAX_IDLE_LIMIT);
            if (newMax > currentMax) {
                dataSource.setMaximumPoolSize(newMax);
                dataSource.setMinimumIdle(newMin);
                log.warn("High load detected (avgWait={}ms, usage={}%) → Scaled UP pool: max={}→{}, minIdle={}→{}",
                        (long) avgWait, Math.round(usageRatio * 100),
                        currentMax, newMax, currentMin, newMin);
            }
        }

        // === SCALE DOWN ===
        else if (avgWait < LOW_WAIT_THRESHOLD_MS && usageRatio < LOW_USAGE_RATIO) {
            int newMax = Math.max(currentMax - 10, MIN_POOL_LIMIT);
            int newMin = Math.max(currentMin - 2, MIN_IDLE_LIMIT);
            if (newMax < currentMax) {
                dataSource.setMaximumPoolSize(newMax);
                dataSource.setMinimumIdle(newMin);
                log.info("Low load detected (avgWait={}ms, usage={}%) → Scaled DOWN pool: max={}→{}, minIdle={}→{}",
                        (long) avgWait, Math.round(usageRatio * 100),
                        currentMax, newMax, currentMin, newMin);
            }
        }
    }
}
