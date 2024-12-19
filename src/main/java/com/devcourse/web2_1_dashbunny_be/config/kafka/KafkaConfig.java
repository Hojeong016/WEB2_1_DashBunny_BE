/*
package com.devcourse.web2_1_dashbunny_be.config.kafka;

import com.devcourse.web2_1_dashbunny_be.feature.admin.kafka.CouponRequestMessage;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ContainerProperties;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

*/
/**
 * 카프카 설정.
 *//*

@EnableKafka
@Configuration
public class KafkaConfig {
  @Bean
  public ProducerFactory<String, CouponRequestMessage> producerFactory() {
    Map<String, Object> configProps = new HashMap<>();
    configProps.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
    configProps.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configProps.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    configProps.put(JsonSerializer.ADD_TYPE_INFO_HEADERS, false); // Type 정보 포함 여부
    return new DefaultKafkaProducerFactory<>(configProps);
  }

  @Bean
  public KafkaTemplate<String, CouponRequestMessage> kafkaTemplate() {
    return new KafkaTemplate<>(producerFactory());
  }

//  @Bean
//  public ConcurrentKafkaListenerContainerFactory<String, CouponRequestMessage> kafkaListenerContainerFactory(
//          ConsumerFactory<String, CouponRequestMessage> consumerFactory) {
//    ConcurrentKafkaListenerContainerFactory<String, CouponRequestMessage> factory = new ConcurrentKafkaListenerContainerFactory<>();
//    factory.setConsumerFactory(consumerFactory);
//    factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL); // MANUAL AckMode 설정
//    return factory;
//  }
}
*/
