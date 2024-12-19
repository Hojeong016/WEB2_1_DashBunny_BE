/*
package com.devcourse.web2_1_dashbunny_be.feature.admin.kafka;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaProducerService {
  private final KafkaTemplate<String, CouponRequestMessage> kafkaTemplate;


  public void sendCouponDownloadRequest(Long couponId, Long userId) {
    CouponRequestMessage message = new CouponRequestMessage(couponId, userId);
    // 쿠폰 ID를 키로 사용하여 파티션을 고정
    kafkaTemplate.send("coupon-download-topic", couponId.toString(), message);
    log.info("Sent coupon download request: {}", message);
  }

  // DLQ로 메시지를 전송하는 메서드
  public void sendToDLQ(CouponRequestMessage message) {
    kafkaTemplate.send("coupon-download-dlq-topic", message.getCouponId().toString(), message);
    log.info("Sent message to DLQ: {}", message);
  }


}
//역할: 쿠폰 다운로드 요청을 Kafka의 coupon-download-topic 토픽으로 보냄
//메시지 포맷:CouponRequestMessage 객체를 JSON으로 직렬화하여 전달.
//장점:Kafka가 요청을 받아들이는 대기열 역할을 하여 서버 부하를 분산한다.*/
