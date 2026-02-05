# Redis 缓存 `LocalDateTime` 序列化 500 错误修复

## 问题现象

在把 `HouseStageResponse` 放入 Redis 后，接口返回 500：

- `Could not write JSON`
- `Java 8 date/time type java.time.LocalDateTime not supported by default`

根因是 Redis 使用的 JSON 序列化器没有注册 `JavaTimeModule`，导致 `LocalDateTime` 无法被 Jackson 正常处理。

## 推荐修复

### 1) 引入依赖（Spring Boot 通常已有）

```xml
<dependency>
  <groupId>com.fasterxml.jackson.datatype</groupId>
  <artifactId>jackson-datatype-jsr310</artifactId>
</dependency>
```

### 2) 为 RedisTemplate 配置带 JavaTimeModule 的 Jackson 序列化器

```java
@Configuration
public class RedisConfig {

    @Bean
    public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
        RedisTemplate<String, Object> template = new RedisTemplate<>();
        template.setConnectionFactory(connectionFactory);

        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Jackson2JsonRedisSerializer<Object> serializer =
                new Jackson2JsonRedisSerializer<>(objectMapper, Object.class);

        template.setKeySerializer(new StringRedisSerializer());
        template.setValueSerializer(serializer);
        template.setHashKeySerializer(new StringRedisSerializer());
        template.setHashValueSerializer(serializer);
        template.afterPropertiesSet();
        return template;
    }
}
```

> 如果你项目里已经有全局 `ObjectMapper`，建议直接复用同一个 mapper，避免 HTTP 序列化和 Redis 序列化规则不一致。

## 临时规避方案（不推荐长期使用）

- 改成缓存 `String`（先手动 `writeValueAsString` 再存 Redis）
- DTO 中把时间字段改成字符串（如 `yyyy-MM-dd HH:mm:ss`）

## 你当前代码里的额外注意点

你现在 `cacheKey` 写成：

```java
String cacheKey = CACHE_PREFIX + "materials:" +  ":" + houseId;
```

中间会多一个 `:`，建议改成更清晰的一种：

```java
String cacheKey = CACHE_PREFIX + "materials:" + houseId;
```

或：

```java
String cacheKey = String.format("%smaterials:%d", CACHE_PREFIX, houseId);
```

避免后续排查缓存命中问题时被 key 格式干扰。
