package com.sapient.idempotencypoc.redis;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Service;

@Slf4j
@Service
public class RedisService {

    @Autowired
    private StringRedisTemplate template;

    public String getCacheValue(String key) {
        ValueOperations<String, String> ops = this.template.opsForValue();
        return ops.get(key);
    }

    public void setCacheKeyValue(String key, String value) {
        ValueOperations<String, String> ops = this.template.opsForValue();
        ops.set(key, value);
        log.info("successfully stored in cache");
    }
}
