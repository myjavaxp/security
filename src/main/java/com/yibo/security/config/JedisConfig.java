package com.yibo.security.config;

import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory;
import redis.clients.jedis.JedisPool;

import java.util.Objects;

@Configuration
public class JedisConfig {
    @Bean
    public JedisPool jedisPool(JedisConnectionFactory redisConnectionFactory) {
        GenericObjectPoolConfig poolConfig = redisConnectionFactory.getPoolConfig();
        return new JedisPool(poolConfig,
                redisConnectionFactory.getHostName(),
                redisConnectionFactory.getPort(),
                ((Long) Objects.requireNonNull(poolConfig).getMaxWaitMillis()).intValue(),
                null,
                redisConnectionFactory.getDatabase());
    }
}