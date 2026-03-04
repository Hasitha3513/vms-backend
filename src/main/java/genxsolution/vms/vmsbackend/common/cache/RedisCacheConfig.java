package genxsolution.vms.vmsbackend.common.cache;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.cache.CacheManager;
import org.springframework.cache.concurrent.ConcurrentMapCacheManager;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.serializer.RedisSerializationContext;

import java.time.Duration;
import java.util.Map;

@Configuration
public class RedisCacheConfig {

    @Bean
    public CacheManager cacheManager(ObjectProvider<RedisConnectionFactory> connectionFactoryProvider) {
        RedisConnectionFactory connectionFactory = connectionFactoryProvider.getIfAvailable();
        if (connectionFactory == null) {
            return new ConcurrentMapCacheManager(
                    CacheNames.LOOKUP_ENUM_VALUES,
                    CacheNames.ORG_DROPDOWN_CORE,
                    CacheNames.ORG_DROPDOWN_ME,
                    CacheNames.COMPANY_LIST,
                    CacheNames.COMPANY_BY_ID,
                    CacheNames.BRANCH_LIST,
                    CacheNames.BRANCH_BY_ID
            );
        }

        LenientRedisValueSerializer serializer = new LenientRedisValueSerializer();
        RedisCacheConfiguration defaults = RedisCacheConfiguration.defaultCacheConfig()
                .serializeValuesWith(RedisSerializationContext.SerializationPair.fromSerializer(serializer))
                .disableCachingNullValues()
                .computePrefixWith(cacheName -> "v5::" + cacheName + "::")
                .entryTtl(Duration.ofSeconds(30));

        Map<String, RedisCacheConfiguration> perCacheTtls = Map.of(
                CacheNames.LOOKUP_ENUM_VALUES, defaults.entryTtl(Duration.ofHours(6)),
                CacheNames.ORG_DROPDOWN_CORE, defaults.entryTtl(Duration.ofMinutes(30)),
                CacheNames.ORG_DROPDOWN_ME, defaults.entryTtl(Duration.ofMinutes(5)),
                CacheNames.COMPANY_LIST, defaults.entryTtl(Duration.ofSeconds(20)),
                CacheNames.COMPANY_BY_ID, defaults.entryTtl(Duration.ofMinutes(5)),
                CacheNames.BRANCH_LIST, defaults.entryTtl(Duration.ofSeconds(20)),
                CacheNames.BRANCH_BY_ID, defaults.entryTtl(Duration.ofMinutes(5))
        );

        return RedisCacheManager.builder(connectionFactory)
                .cacheDefaults(defaults)
                .withInitialCacheConfigurations(perCacheTtls)
                .build();
    }
}
