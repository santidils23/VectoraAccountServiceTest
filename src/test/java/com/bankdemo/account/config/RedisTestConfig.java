package com.bankdemo.account.config;

import org.mockito.Mockito;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Primary;
import org.springframework.context.annotation.Profile;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;

@TestConfiguration
@Profile("test")
public class RedisTestConfig {

    @Bean
    @Primary
    public RedisConnectionFactory redisConnectionFactory() {
        RedisConnectionFactory mockFactory = Mockito.mock(RedisConnectionFactory.class);
        RedisConnection mockConnection = Mockito.mock(RedisConnection.class);

        // Configurar el comportamiento del mock para getConnection()
        when(mockFactory.getConnection()).thenReturn(mockConnection);

        // Asegurar que close() no cause errores
        doNothing().when(mockConnection).close();

        return mockFactory;
    }

    @Bean
    @Primary
    public RedisTemplate<Object, Object> redisTemplate() {
        RedisTemplate<Object, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(redisConnectionFactory());
        return template;
    }
}