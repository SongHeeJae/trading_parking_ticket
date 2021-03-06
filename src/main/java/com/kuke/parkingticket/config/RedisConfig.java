package com.kuke.parkingticket.config;

import com.kuke.parkingticket.common.cache.CacheKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.CacheKeyPrefix;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializationContext;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;
import java.util.HashMap;
import java.util.Map;

@EnableCaching
@Configuration
public class RedisConfig {

    @Value("${spring.redis.port}")
    private int port;

    @Value("${spring.redis.host}")
    private String host;

    @Bean
    public RedisConnectionFactory redisConnectionFactory() {
        return new LettuceConnectionFactory(host, port);
    }

    @Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        return redisTemplate;
    }

    @Bean
    public CacheManager cacheManager() {
        RedisCacheConfiguration configuration = RedisCacheConfiguration.defaultCacheConfig()
                .disableCachingNullValues()
                .entryTtl(Duration.ofSeconds(CacheKey.DEFAULT_EXPIRE_SEC))
                .computePrefixWith(CacheKeyPrefix.simple())
                .serializeKeysWith(RedisSerializationContext.SerializationPair.fromSerializer(new StringRedisSerializer()));

        // 캐시키별 default 유효기간 설정
        Map<String, RedisCacheConfiguration> cacheConfigurations = new HashMap<>();
        cacheConfigurations.put(CacheKey.USER, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.USER_EXPIRE_SEC)));
        cacheConfigurations.put(CacheKey.TICKET, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.TICKET_EXPIRE_SEC)));
        cacheConfigurations.put(CacheKey.TICKETS, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.TICKET_EXPIRE_SEC)));
        cacheConfigurations.put(CacheKey.REGIONS_WITH_TOWNS, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.REGIONS_WITH_TOWNS_EXPIRE_SEC)));
        cacheConfigurations.put(CacheKey.COMMENTS, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.COMMENTS_EXPIRE_SEC)));
        cacheConfigurations.put(CacheKey.SALES_HISTORIES, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.SALES_HISTORIES_EXPIRE_SEC)));
        cacheConfigurations.put(CacheKey.PURCHASE_HISTORIES, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.PURCHASE_HISTORIES_EXPIRE_SEC)));
        cacheConfigurations.put(CacheKey.TYPED_REVIEWS, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.TYPED_REVIEWS_EXPIRE_SEC)));
        cacheConfigurations.put(CacheKey.TYPING_REVIEWS, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.TYPING_REVIEWS_EXPIRE_SEC)));
        cacheConfigurations.put(CacheKey.SENT_MESSAGES, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.SENT_MESSAGES_EXPIRE_SEC)));
        cacheConfigurations.put(CacheKey.RECEIVED_MESSAGES, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.RECEIVED_MESSAGES_EXPIRE_SEC)));
        cacheConfigurations.put(CacheKey.MESSAGE, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.MESSAGE_EXPIRE_SEC)));
        cacheConfigurations.put(CacheKey.REGIONS, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.REGIONS_EXPIRE_SEC)));
        cacheConfigurations.put(CacheKey.TOWNS, RedisCacheConfiguration.defaultCacheConfig().entryTtl(Duration.ofSeconds(CacheKey.TOWNS_EXPIRE_SEC)));

        return RedisCacheManager.RedisCacheManagerBuilder
                .fromConnectionFactory(redisConnectionFactory())
                .cacheDefaults(configuration)
                .withInitialCacheConfigurations(cacheConfigurations)
                .build();
    }
}
