/*
package com.devcourse.web2_1_dashbunny_be.feature.user.userCoupon.controller;

import com.devcourse.web2_1_dashbunny_be.domain.user.User;
import com.devcourse.web2_1_dashbunny_be.domain.user.role.IssuedCouponType;
import com.devcourse.web2_1_dashbunny_be.exception.CustomException;
import com.devcourse.web2_1_dashbunny_be.feature.admin.adminCoupon.service.AdminCouponService;
import com.devcourse.web2_1_dashbunny_be.feature.admin.kafka.KafkaProducerService;
import com.devcourse.web2_1_dashbunny_be.feature.owner.ownerCoupon.service.OwnerCouponService;
import com.devcourse.web2_1_dashbunny_be.feature.user.service.CustomUserDetailsService;
import com.devcourse.web2_1_dashbunny_be.feature.user.service.UserService;
import com.devcourse.web2_1_dashbunny_be.feature.user.userCoupon.dto.FirstComeCouponResponseWrapper;
import com.devcourse.web2_1_dashbunny_be.feature.user.userCoupon.dto.GeneralCouponListResponseDto;
import com.devcourse.web2_1_dashbunny_be.feature.user.userCoupon.dto.OwnerCouponListResponseDto;
import com.devcourse.web2_1_dashbunny_be.feature.user.userCoupon.dto.UserCouponListResponseDto;
import com.devcourse.web2_1_dashbunny_be.feature.user.userCoupon.service.UserCouponService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.Collections;
import java.util.List;

*/
/**
 * 사용자 쿠폰 컨트롤러.
 *//*


@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user/coupon")
public class UserCouponController {
  private final UserCouponService userCouponService;
  private final UserService userService;
  private final KafkaProducerService kafkaProducerService;

  */
/**
   * 관리자 발급한 일반 쿠폰 목록 조회 api (GET).
   *//*

  @GetMapping("/general")
  public ResponseEntity<List<GeneralCouponListResponseDto>> getGeneralCoupon(@RequestHeader("Authorization") String authorizationHeader) {
    User currentUser = userService.getCurrentUser(authorizationHeader);
    List<GeneralCouponListResponseDto> coupons = userCouponService.findActiveRegularCoupons(currentUser);
    return ResponseEntity.status(HttpStatus.OK).body(coupons);
  }

  */
/**
   * 관리자 발급한 선착순 쿠폰  조회 api (GET).
   *//*

  @GetMapping("/first-come")
  public ResponseEntity<?> getFirstComeCoupon(@RequestHeader("Authorization") String authorizationHeader) {
    User currentUser = userService.getCurrentUser(authorizationHeader);
    FirstComeCouponResponseWrapper response = userCouponService.findActiveFirstComeCoupon(currentUser);

    if (response.getCoupon() != null) {
      return ResponseEntity.ok(response.getCoupon());
    } else if (response.getMessage() != null) {
      return ResponseEntity.ok(Collections.singletonMap("message", response.getMessage()));
    } else {
      return ResponseEntity.noContent().build(); // 쿠폰이 활성화되지 않은 경우
    }
  }

  */
/**
   * 사장님이 발급한 쿠폰 목록 조회 api (GET).
   *//*

  @GetMapping("/owner/{storeId}")
  public ResponseEntity<List<OwnerCouponListResponseDto>> getOwnerCoupon(@PathVariable String storeId
          , @RequestHeader("Authorization") String authorizationHeader) {
    String phone = userService.getCurrentUser(authorizationHeader).getPhone();

    List<OwnerCouponListResponseDto> ownerCoupons = userCouponService.findActiveOwnerCoupons(storeId);
    return ResponseEntity.status(HttpStatus.OK).body(ownerCoupons);
  }

  */
/**
   * 일반 쿠폰 다운로드 api (POST).
   *//*

  @PostMapping("/download/general/{couponId}")
  public ResponseEntity<?> downloadGeneralCoupon(@PathVariable Long couponId
          , @RequestHeader("Authorization") String authorizationHeader) {
    User currentUser = userService.getCurrentUser(authorizationHeader);

    //현재 사용자의 userId를 가져와야함
    userCouponService.downloadCoupon(couponId, IssuedCouponType.ADMIN,currentUser);
    return ResponseEntity.ok(Collections.singletonMap("message", "쿠폰이 발급되었습니다!"));

  }

  */
/**
   * 가게 쿠폰 다운로드 api (POST).
   *//*

  @PostMapping("/download/owner/{couponId}")
  public ResponseEntity<?> downloadOwnerCoupon(@PathVariable Long couponId
          , @RequestHeader("Authorization") String authorizationHeader) {
    User currentUser = userService.getCurrentUser(authorizationHeader);
    //현재 사용자의 userId를 가져와야함
    userCouponService.downloadCoupon(couponId, IssuedCouponType.OWNER,currentUser);
    return ResponseEntity.ok(Collections.singletonMap("message", "쿠폰이 발급되었습니다!"));

  }

  */
/**
   * 선착순 쿠폰 다운로드 api (POST).
   *//*

  @PostMapping("/download/first-come/{couponId}") //, HttpServletRequest request
  public ResponseEntity<?> downloadFirstComeCoupon(@PathVariable Long couponId
          , @RequestHeader("Authorization") String authorizationHeader) {
    User currentUser = userService.getCurrentUser(authorizationHeader);

    // 중복 다운로드 확인
    if (userCouponService.isAlreadyDownloaded(currentUser.getUserId(), couponId)) {
      return ResponseEntity.status(HttpStatus.BAD_REQUEST)
              .body(Collections.singletonMap("message", "중복 다운로드가 불가능한 쿠폰입니다."));
    }

    try {
      // Kafka Producer로 메시지 전송
      kafkaProducerService.sendCouponDownloadRequest(couponId, currentUser.getUserId());
      log.info("쿠폰 발급 요청 전송 완료: User {}, Coupon ID {}", currentUser.getUserId(), couponId);

      // 요청 접수 메시지 반환
      return ResponseEntity.ok(Collections.singletonMap("message", "쿠폰 발급 요청이 접수되었습니다."));
    }catch (CustomException e) {
      return ResponseEntity.status(e.getStatus())
              .body(Collections.singletonMap("message", e.getMessage()));
    }  catch (Exception e) {
      log.error("쿠폰 발급 요청 실패: {}", e.getMessage());
      return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
              .body(Collections.singletonMap("message", "쿠폰 발급 요청 처리 중 문제가 발생했습니다."));
    }
  }



  */
/**
   * 사용자 쿠폰함 쿠폰 목록 조회 api (GET).
   *//*

  @GetMapping("/box")
  public ResponseEntity<List<UserCouponListResponseDto>> getBoxCoupon(
           @RequestHeader("Authorization") String authorizationHeader
  ) {
    User currentUser = userService.getCurrentUser(authorizationHeader);

    List<UserCouponListResponseDto> coupons = userCouponService.findNotUsedCoupons(currentUser);
    return ResponseEntity.status(HttpStatus.OK).body(coupons);
  }





}
*/
