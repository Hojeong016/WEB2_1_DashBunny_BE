/*
package com.devcourse.web2_1_dashbunny_be.config.kafka;

import jakarta.annotation.PostConstruct;
import org.apache.kafka.clients.admin.NewTopic;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.TopicBuilder;
import org.springframework.kafka.core.KafkaAdmin;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaAdminConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @PostConstruct
  public void logBootstrapServers() {
    System.out.println("Kafka Bootstrap Servers: " + bootstrapServers);
  }

  // KafkaAdmin 빈 생성
  @Bean
  public KafkaAdmin kafkaAdmin() {
    Map<String, Object> configs = new HashMap<>();
    configs.put(org.apache.kafka.clients.admin.AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    return new KafkaAdmin(configs);
  }

  // 일반 토픽 생성
  @Bean
  public NewTopic couponDownloadTopic() {
    return TopicBuilder.name("coupon-download-topic")
            .partitions(3)
            .replicas(1)
            .build();
  }

  // DLQ 토픽 생성
  @Bean
  public NewTopic couponDownloadDlqTopic() {
    return TopicBuilder.name("coupon-download-dlq-topic")
            .partitions(3)
            .replicas(1)
            .build();
  }
}
*/
