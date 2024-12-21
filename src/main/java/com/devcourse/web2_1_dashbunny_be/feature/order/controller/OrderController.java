package com.devcourse.web2_1_dashbunny_be.feature.order.controller;

import com.devcourse.web2_1_dashbunny_be.domain.user.User;
import com.devcourse.web2_1_dashbunny_be.feature.order.controller.dto.*;
import com.devcourse.web2_1_dashbunny_be.feature.order.controller.dto.user.UserOrderInfoRequestDto;
import com.devcourse.web2_1_dashbunny_be.feature.order.service.OrderService;
import com.google.protobuf.InvalidProtocolBufferException;
import com.devcourse.web2_1_dashbunny_be.feature.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

/**
 * 사용자로 부터 주문 요청을 받고 처리하는 controller.
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/order")
public class OrderController {

  private final String ERROR_TOPIC = "/topic/order/error";
  private final SimpMessagingTemplate messageTemplate;
  private final OrderService orderService;
  private final UserService userService;
  /**
   * 사용자에게 주문 요청이 옴과 동시에 주문 접수 요청에 대한 알람을 보냅니다.
   */
  @PostMapping("/create")
  public CompletableFuture<ResponseEntity<String>> creatOrder(
          @RequestBody OrderInfoRequestDto orderInfoRequestDto) {
    return orderService.creatOrder(orderInfoRequestDto)
            .thenApply(order -> {
              String topic = String.format("/topic/userOrder/%s/%s",
                                            orderInfoRequestDto.getStoreId(), order.getOrderId());
                Map<String, Object> payload = new HashMap<>();
                payload.put("type", "ORDER_PENDING");
                payload.put("message", "주문 접수 중");
                payload.put("data", Map.of(
                        "orderId", order.getOrderId(),
                        "storeId", orderInfoRequestDto.getStoreId()));
              messageTemplate.convertAndSend(topic, "주문 접수 중");
              return ResponseEntity.ok("주문 접수 요청에 성공했습니다." + order.getOrderStatus());
            })
    .exceptionally(ex -> {
        log.error("주문 처리 중 예외 발생", ex);
        // 실패 알림도 동일하게 타입 지정
        Map<String, Object> errorPayload = new HashMap<>();
        errorPayload.put("type", "ORDER_FAILED"); // 실패 타입 지정
        errorPayload.put("message", "주문 접수 실패");
        errorPayload.put("data", Map.of(
                "error", ex.getMessage() // 실패 원인만 포함
        ));
        messageTemplate.convertAndSend(ERROR_TOPIC, errorPayload);
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body("주문 처리 중 문제가 발생했습니다: " + ex.getMessage());
    });
  }

  /**
  * 사장님 : 주문 수락.
  */
  @PostMapping("/store/{orderId}/accept")
  public CompletableFuture<ResponseEntity<AcceptOrdersResponseDto>> acceptOrder(
          @PathVariable("orderId") Long orderId,
          @RequestBody OrderAcceptRequestDto acceptRequestDto) {
    return orderService.acceptOrder(acceptRequestDto)
              .thenApply (acceptOrdersResponseDto -> {
                String topic = String.format("/topic/userOrder/%s/%s",
                                              acceptOrdersResponseDto.getStoreId(), orderId);
                  Map<String, Object> payload = new HashMap<>();
                  payload.put("type", "IN_PROGRESS");
                  payload.put("message", "메뉴가 접수되었습니다. 조리중");
                  payload.put("data", acceptOrdersResponseDto);
                messageTemplate.convertAndSend(topic, payload);
                return ResponseEntity.ok(acceptOrdersResponseDto);
              });
  }

  /**
  * 사장님 : 주문 거절.
  */
  @PostMapping("/{orderId}/decline")
  public CompletableFuture<ResponseEntity<DeclineOrdersResponseDto>> declineOrder(
          @PathVariable("orderId") Long orderId,
          @RequestBody OrderDeclineRequestDto declineRequestDto) {
    return orderService.declineOrder(declineRequestDto)
              .thenApply (declineOrdersResponseDto -> {
                String topic = String.format("/topic/userOrder/%s/%s",
                                             declineOrdersResponseDto.getStoreId(), orderId);
                  Map<String, Object> payload = new HashMap<>();
                  payload.put("type", "DECLINED");
                  payload.put("message", "주문이 취소되었습니다. 거절 사유 :" + declineOrdersResponseDto.getDeclineReasonType().getDescription() );
                  payload.put("data", declineOrdersResponseDto);
                messageTemplate.convertAndSend(topic, payload);
                return ResponseEntity.ok(declineOrdersResponseDto);
              });
  }

  /**
  * 사장님 주문 관리 페이지.
   * 모든 오더 객체의 정보를 리스트로 담아서 반환합니다.
   * 관리 페이지가 자주 갱신되는 데이터를 실시간으로 반영-> 비동기
   * 프로토버퍼 객체 -> Json으로 변환하여 반환
  */
  @GetMapping("store/order-list/{storeId}")
  public ResponseEntity<String> getOrderList(@PathVariable("storeId") String storeId) throws InvalidProtocolBufferException {
      String response = orderService.getOrdersList(storeId);
      log.info(response);
      return new ResponseEntity<>(response, HttpStatus.OK);
  }

  @GetMapping("/list")
  public ResponseEntity<List<UserOrderInfoRequestDto>> listOrders(@RequestHeader("Authorization") String authorizationHeader) {
    User currentUser = userService.getCurrentUser(authorizationHeader);
    List<UserOrderInfoRequestDto> userOrderLost = orderService.getUserOrderInfoList(currentUser.getPhone());
    return ResponseEntity.ok(userOrderLost);
  }

  @PostMapping("/rating")
  public ResponseEntity<Void> ratingCount(@RequestHeader("Authorization") String authorizationHeader,
                                          @RequestBody OrderRatingResponseDto orders) {
      orderService.increaseRating(orders);
    return ResponseEntity.ok().build();
  }

}
