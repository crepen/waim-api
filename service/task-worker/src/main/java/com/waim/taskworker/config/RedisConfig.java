package com.waim.taskworker.config;


import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.resource.ClientResources;
import io.lettuce.core.resource.Delay;
import lombok.extern.slf4j.Slf4j;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

@Configuration
@Slf4j
public class RedisConfig {
    @Value("${spring.data.redis.host:nohost}")
    private String redisHost;

    @Value("${spring.data.redis.port:0}")
    private int redisPort;

    @Value("${spring.data.redis.password}")
    private String redisPassword;

    @Bean
    public RedissonClient redissonClient() {
        Config config = new Config();
        // 주소 형식: redis://host:port (비암호화) 또는 rediss://host:port (SSL/TLS)
        String address = "redis://" + redisHost + ":" + redisPort;

        config.useSingleServer()
                .setAddress(address)
                .setPassword(redisPassword)
                .setConnectionMinimumIdleSize(5)
                .setConnectionPoolSize(64);

        return Redisson.create(config);
    }

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {

        log.info("[REDIS FACTORY] Connect Redis : {}:{}", redisHost, redisPort);

        // 1. 재연결 지연 전략 설정 (ClientResources)
        ClientResources resources = ClientResources.builder()
                .reconnectDelay(Delay.exponential(
                        Duration.ofMillis(100),
                        Duration.ofSeconds(30),
                        2,
                        TimeUnit.MILLISECONDS)
                )
                .build();

        resources.eventBus().get().subscribe(event -> {
            if (event instanceof io.lettuce.core.event.connection.ConnectionActivatedEvent) {
                log.info("[REDIS EVENT] Connect: {}", event);
            } else if (event instanceof io.lettuce.core.event.connection.DisconnectedEvent) {
                log.warn("[REDIS EVENT] Disconnect: {}", event);
            } else if (event instanceof io.lettuce.core.event.connection.ReconnectFailedEvent) {
                log.error("[REDIS EVENT] Failed retry connect: {}", event);
            }
        });

        // 2. 소켓 옵션 설정
        SocketOptions socketOptions = SocketOptions.builder()
                .connectTimeout(Duration.ofSeconds(1))
                .keepAlive(true)
                .build();

        // 3. 클라이언트 옵션 설정 (reconnectDelay 제외)
        ClientOptions clientOptions = ClientOptions.builder()
                .socketOptions(socketOptions)
                .disconnectedBehavior(ClientOptions.DisconnectedBehavior.REJECT_COMMANDS)
                .build();

        // 4. ClientResources와 ClientOptions를 통합 설정
        LettuceClientConfiguration clientConfig = LettuceClientConfiguration.builder()
                .clientResources(resources) // 여기서 재연결 전략 주입
                .clientOptions(clientOptions)
                .commandTimeout(Duration.ofSeconds(2))
                .build();

        RedisStandaloneConfiguration serverConfig = new RedisStandaloneConfiguration();
        serverConfig.setHostName(redisHost);
        serverConfig.setPort(redisPort);
        serverConfig.setPassword(redisPassword);

        return new LettuceConnectionFactory(serverConfig, clientConfig);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        // 4.0 대응 표준: RedisSerializer의 정적 팩토리 메서드 활용
        // .json()에 인자를 넣지 못하는 버전이라면 기본 설정을 따르되
        // 인터페이스 타입을 통해 구현체 의존성을 제거합니다.
        RedisSerializer<Object> jsonSerializer = RedisSerializer.json();
        RedisSerializer<String> stringSerializer = RedisSerializer.string();

        template.setKeySerializer(stringSerializer);
        template.setValueSerializer(jsonSerializer);
        template.setHashKeySerializer(stringSerializer);
        template.setHashValueSerializer(jsonSerializer);

        template.afterPropertiesSet();
        return template;
    }
}

