/*
package com.devcourse.web2_1_dashbunny_be.config.kafka;

import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.Config;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.Properties;

*/
/**
 * kafka 메세지 유지 기한 설정.
 *//*

@Configuration
public class KafkaRetentionConfig {

  //@Bean
  public void updateRetentionPolicy() throws Exception {
    Properties config = new Properties();
    config.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");

    //토픽이 coupon-download-topic인 메세지의 유효기간 설정
    try (AdminClient adminClient = AdminClient.create(config)) {
      ConfigResource topicResource = new ConfigResource(ConfigResource.Type.TOPIC, "coupon-download-topic");

      Config configUpdate = new Config(Collections.singletonList(
              new ConfigEntry("retention.ms", "604800000") // 7일(밀리초 단위)
      ));

      adminClient.alterConfigs(Collections.singletonMap(topicResource, configUpdate)).all().get();
      System.out.println("Retention policy updated for coupon-download-topic!");
    }
  }
}
*/
