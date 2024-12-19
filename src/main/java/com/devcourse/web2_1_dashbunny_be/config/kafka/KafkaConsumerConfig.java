/*
package com.devcourse.web2_1_dashbunny_be.config.kafka;


import com.devcourse.web2_1_dashbunny_be.feature.admin.kafka.CouponRequestMessage;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfig {

  @Value("${spring.kafka.bootstrap-servers}")
  private String bootstrapServers;

  @Bean
  public ConsumerFactory<String, CouponRequestMessage> consumerFactory() {
    Map<String, Object> props = new HashMap<>();
    props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
    props.put(ConsumerConfig.GROUP_ID_CONFIG, "coupon-group");
    props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    props.put(JsonDeserializer.TRUSTED_PACKAGES, "*");
    props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false); // 수동 커밋 설정
    //props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 100); // 배치 사이즈 설정
    props.put(ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 1); // 한 번에 하나의 메시지 처리
    return new DefaultKafkaConsumerFactory<>(props, new StringDeserializer(),
              new JsonDeserializer<>(CouponRequestMessage.class));
  }

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, CouponRequestMessage> kafkaListenerContainerFactory() {
    ConcurrentKafkaListenerContainerFactory<String, CouponRequestMessage> factory =
            new ConcurrentKafkaListenerContainerFactory<>();
    factory.setConsumerFactory(consumerFactory());
    //factory.setBatchListener(true); // 배치 리스너 활성화
    factory.setConcurrency(3); // 동시 처리 Consumer 개수를 3로 제한
    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL);
    return factory;
  }
}
*/
