package com.devcourse.web2_1_dashbunny_be.config;

import com.devcourse.web2_1_dashbunny_be.domain.owner.MenuManagement;
import com.devcourse.web2_1_dashbunny_be.domain.user.Orders;
import com.order.generated.OrdersProtobuf;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

@Configuration
public class RedisConfig {


  @Value("${spring.data.redis.host}")
  private String redisHost;
  @Value("${spring.data.redis.port}")
  private String redisPort;

  @Bean
  public RedisConnectionFactory redisConnectionFactory () {
    RedisStandaloneConfiguration redisStandaloneConfiguration = new RedisStandaloneConfiguration();
    redisStandaloneConfiguration.setHostName(redisHost);
    redisStandaloneConfiguration.setPort(Integer.parseInt(redisPort));
    return new LettuceConnectionFactory(redisStandaloneConfiguration);
  }

  @Bean(name = "genericRedisTemplate")
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    // Key를 String으로 직렬화
    template.setKeySerializer(new StringRedisSerializer());
    // Value를 JSON으로 직렬화
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());

    return template;
    }

  // MenuManagement 용도의 RedisTemplate
  @Bean(name = "menuRedisTemplate")
  public RedisTemplate<String, MenuManagement> menuRedisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, MenuManagement> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);

    // Key를 String으로 직렬화
    template.setKeySerializer(new StringRedisSerializer());

    // Value를 JSON으로 직렬화
    Jackson2JsonRedisSerializer<MenuManagement> serializer = new Jackson2JsonRedisSerializer<>(MenuManagement.class);
    template.setValueSerializer(serializer);
    template.setHashValueSerializer(serializer);

    // After properties
    template.afterPropertiesSet();
    return template;
    }

  // MenuManagement 용도의 RedisTemplate
  @Bean(name = "orderRedisTemplate")
  public RedisTemplate<String, OrdersProtobuf.Orders> orderRedisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, OrdersProtobuf.Orders> redisTemplate = new RedisTemplate<>();
    redisTemplate.setConnectionFactory(connectionFactory);

    // Protobuf 직렬화기 생성
    ProtobufRedisSerializer<OrdersProtobuf.Orders> protobufSerializer = new ProtobufRedisSerializer<>(OrdersProtobuf.Orders.parser());

    // 직렬화 설정
    redisTemplate.setKeySerializer(RedisSerializer.string());
    redisTemplate.setValueSerializer(protobufSerializer);
    redisTemplate.setHashKeySerializer(RedisSerializer.string());
    redisTemplate.setHashValueSerializer(protobufSerializer);

    redisTemplate.afterPropertiesSet();
    return redisTemplate;
  }

  @Bean
  public HashOperations<String, String, OrdersProtobuf.Orders> hash(@Qualifier("orderRedisTemplate") RedisTemplate<String, OrdersProtobuf.Orders> redisTemplate) {
    return redisTemplate.opsForHash();
  }

  @Bean
  public HashOperations<String, String, Object> hashOperations(RedisTemplate<String, Object> redisTemplate) {
    return redisTemplate.opsForHash();
  }
}

