/*
package com.devcourse.web2_1_dashbunny_be.feature.admin.kafka;

import com.devcourse.web2_1_dashbunny_be.domain.admin.AdminCoupon;
import com.devcourse.web2_1_dashbunny_be.domain.user.UserCoupon;
import com.devcourse.web2_1_dashbunny_be.domain.user.role.IssuedCouponType;
import com.devcourse.web2_1_dashbunny_be.exception.CustomException;
import com.devcourse.web2_1_dashbunny_be.feature.admin.adminCoupon.repository.AdminCouponRepository;
import com.devcourse.web2_1_dashbunny_be.feature.user.service.UserService;
import com.devcourse.web2_1_dashbunny_be.feature.user.userCoupon.repository.UserCouponRepository;
import com.devcourse.web2_1_dashbunny_be.feature.user.userCoupon.service.UserCouponService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.http.HttpStatus;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.Acknowledgment;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class KafkaConsumerService {
  private final UserCouponService userCouponService;
  private final UserService userService;
  private final KafkaProducerService kafkaProducerService;
  private final UserCouponRepository userCouponRepository;
  private final AdminCouponRepository adminCouponRepository;
  private final RedisTemplate<String, Object> redisTemplate;

//  @KafkaListener(topics = "coupon-download-topic", groupId = "coupon-group")
//  public void consumeMessage(CouponRequestMessage message) {
//    log.info("Received message: {}", message);
//    // Kafka에서 수신한 메시지를 처리
//    userCouponService.downloadFirstComeCoupon(message.getUserId(), message.getCouponId());
//@KafkaListener(topics = "coupon-download-topic", groupId = "coupon-group")
//  public synchronized void consumeMessage(CouponRequestMessage message, Acknowledgment acknowledgment) {
//    log.info("Received message: {}", message);
//
//    try {
//      // 개별 메시지를 UserCoupon 엔티티로 변환 후 저장
//      UserCoupon userCoupon = UserCoupon.builder()
//              .userId(message.getUserId())
//              .couponId(message.getCouponId())
//              .issuedCouponType(IssuedCouponType.ADMIN)
//              .usedDate(null)
//              .couponUsed(false)
//              .isExpired(false)
//              .build();
//
//      userCouponRepository.save(userCoupon); // 개별 저장
//     // log.info("Saved user coupon: {}", userCoupon);
//      log.info("Processing Kafka message: User {}, Coupon ID {}", message.getUserId(), message.getCouponId());
//      acknowledgment.acknowledge(); // 수동 커밋
//    } catch (Exception e) {
//      log.error("DB 저장 실패: {}", e.getMessage());
//      // 실패한 메시지를 DLQ로 전송
//      kafkaProducerService.sendToDLQ(message);
//      // Redis 발급 개수를 복구
//      userCouponService.decrementCouponCount(message.getCouponId());
//    }
//  }



//  @KafkaListener(topics = "coupon-download-topic", groupId = "coupon-group")
//  public synchronized void consumeMessage(CouponRequestMessage message, Acknowledgment acknowledgment) {
//    log.info("Received message: {}", message);
//
//    try {
//      // Redis 발급 개수 확인 및 증가
//      String redisKey = "firstComeCoupon:" + message.getCouponId();
//      ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
//      Long currentCount = valueOps.increment(redisKey, 1);
//
//      AdminCoupon adminCoupon = adminCouponRepository.findById(message.getCouponId())
//              .orElseThrow(() -> new IllegalArgumentException("해당 쿠폰이 존재하지 않습니다."));
//
//      if (currentCount > adminCoupon.getMaxIssuance()) {
//        log.warn("쿠폰 ID: {}, 발급 한도 초과!", message.getCouponId());
//        throw new CustomException("선착순 쿠폰이 모두 소진되었습니다.", HttpStatus.BAD_REQUEST);
//      }
//
//      // UserCoupon 저장
//      UserCoupon userCoupon = UserCoupon.builder()
//              .userId(message.getUserId())
//              .couponId(message.getCouponId())
//              .issuedCouponType(IssuedCouponType.ADMIN)
//              .couponUsed(false)
//              .build();
//      userCouponRepository.save(userCoupon);
//
//      log.info("쿠폰 발급 성공: User {}, Coupon ID {}", message.getUserId(), message.getCouponId());
//      acknowledgment.acknowledge(); // Kafka 메시지 수동 커밋
//
//    } catch (CustomException e) {
//      // 발급 한도 초과에 대한 예외 처리
//      log.warn("쿠폰 발급 실패: {}", e.getMessage());
//
//      // 실패 메시지를 DLQ로 전송
//      kafkaProducerService.sendToDLQ(message);
//
//      // acknowledgment를 호출하지 않으면 메시지를 재처리
//      //acknowledgment.acknowledge();
//    } catch (Exception e) {
//      // 기타 예외 처리
//      log.error("Unexpected error: {}", e.getMessage());
//
//      // 실패 메시지를 DLQ로 전송
//      kafkaProducerService.sendToDLQ(message);
//
//      // Redis 발급 개수 복구
//      try {
//        String redisKey = "firstComeCoupon:" + message.getCouponId();
//        ValueOperations<String, Object> valueOps = redisTemplate.opsForValue();
//        valueOps.increment(redisKey, -1);
//        log.info("Redis 발급 개수 복구 완료: Coupon ID {}", message.getCouponId());
//      } catch (Exception redisException) {
//        log.error("Redis 발급 개수 복구 실패: {}", redisException.getMessage());
//      }
//
//      acknowledgment.acknowledge();
//    }
//  }
  @KafkaListener(topics = "coupon-download-topic", groupId = "coupon-group")
  public void consumeMessage(CouponRequestMessage message, Acknowledgment acknowledgment) {
    log.info("Received message: {}", message);

    try {
      // 메시지를 UserCouponService로 위임
      userCouponService.processCouponDownloadRequest(message.getCouponId(), message.getUserId());
      acknowledgment.acknowledge(); // Kafka 메시지 수동 커밋
    } catch (CustomException e) {
      log.warn("쿠폰 발급 실패: {}", e.getMessage());
      kafkaProducerService.sendToDLQ(message); // 실패 메시지를 DLQ로 전송
    } catch (Exception e) {
      log.error("Unexpected error: {}", e.getMessage());
      kafkaProducerService.sendToDLQ(message);
    }
  }

//  @KafkaListener(topics = "coupon-download-topic", groupId = "coupon-group", containerFactory = "kafkaListenerContainerFactory")
//  public void consumeMessages(List<CouponRequestMessage> messages) {
//    log.info("Received {} messages", messages.size());
//
//    try {
//      // 메시지를 UserCoupon 엔티티로 변환
//      List<UserCoupon> userCoupons = messages.stream()
//              .map(msg -> UserCoupon.builder()
//                      .userId(msg.getUserId())
//                      .couponId(msg.getCouponId())
//                      .issuedCouponType(IssuedCouponType.ADMIN)
//                      .usedDate(null)
//                      .couponUsed(false)
//                      .isExpired(false)
//                      .build()
//              )
//              .collect(Collectors.toList());
//
//      // 배치로 DB에 저장
//      userCouponService.saveAll(userCoupons);
//      log.info("Batch saved {} user coupons", userCoupons.size());
//    } catch (Exception e) {
//      log.error("DB 저장 실패: {}", e.getMessage());
//      // 실패한 메시지를 DLQ로 전송
//      for (CouponRequestMessage message : messages) {
//        kafkaProducerService.sendToDLQ(message);
//      }
//      // Redis 발급 개수를 복구
//      for (CouponRequestMessage message : messages) {
//        userCouponService.decrementCouponCount(message.getCouponId());
//      }
//    }
//  }

}



//역할:Kafka에서 다운로드 요청 메시지를 수신하고 UserCouponService를 호출해 처리한다.
//장점:요청을 순서대로 처리하여 요청 순서와 발급 순서 일치를 보장한다.*/
