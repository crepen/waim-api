package com.waim.scheduler.domain.system.scheduler;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.health.actuate.endpoint.HealthEndpoint;
import org.springframework.boot.health.contributor.Status;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Slf4j
@RequiredArgsConstructor
@Component
public class SystemStatusScheduler {

    private final JdbcTemplate jdbcTemplate;
    private final RedisConnectionFactory redisConnectionFactory;
    private final HealthEndpoint healthEndpoint;


    @Scheduled(fixedRate = 5*60*1000)
    public void checkSystemHealth(){
        Status dbStatus = Objects.requireNonNull(healthEndpoint.healthForPath("db")).getStatus();
        Status redisStatus = Objects.requireNonNull(healthEndpoint.healthForPath("redis")).getStatus();

        if(dbStatus.equals(Status.DOWN)){
            log.error("Database connect failed.");
        }

        else if(redisStatus.equals(Status.DOWN)){
            log.error("Redis connect failed.");
        }
    }
}
