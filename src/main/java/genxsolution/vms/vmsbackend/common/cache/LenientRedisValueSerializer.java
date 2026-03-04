package genxsolution.vms.vmsbackend.common.cache;

import org.springframework.data.redis.serializer.GenericJacksonJsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.SerializationException;

/**
 * Prevents bad/legacy cache payloads from breaking API calls.
 * If a cached value cannot be deserialized, return null so Spring treats it as cache-miss.
 */
public class LenientRedisValueSerializer implements RedisSerializer<Object> {

    private final GenericJacksonJsonRedisSerializer delegate;

    public LenientRedisValueSerializer() {
        this.delegate = GenericJacksonJsonRedisSerializer.builder()
                .enableUnsafeDefaultTyping()
                .build();
    }

    @Override
    public byte[] serialize(Object value) throws SerializationException {
        return delegate.serialize(value);
    }

    @Override
    public Object deserialize(byte[] bytes) throws SerializationException {
        if (bytes == null || bytes.length == 0) {
            return null;
        }
        try {
            return delegate.deserialize(bytes);
        } catch (Exception ignored) {
            // Legacy/invalid payload -> cache miss
            return null;
        }
    }
}

