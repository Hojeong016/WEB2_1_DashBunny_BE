package com.devcourse.web2_1_dashbunny_be.feature.order.service;

import com.devcourse.web2_1_dashbunny_be.domain.owner.MenuManagement;
import com.devcourse.web2_1_dashbunny_be.domain.owner.StoreFeedBack;
import com.devcourse.web2_1_dashbunny_be.domain.owner.StoreManagement;
import com.devcourse.web2_1_dashbunny_be.domain.user.OrderItem;
import com.devcourse.web2_1_dashbunny_be.domain.user.Orders;
import com.devcourse.web2_1_dashbunny_be.domain.user.User;
import com.devcourse.web2_1_dashbunny_be.domain.user.role.OrderStatus;
import com.devcourse.web2_1_dashbunny_be.feature.order.controller.dto.*;
import com.devcourse.web2_1_dashbunny_be.feature.order.controller.dto.user.UserOrderInfoRequestDto;
import com.devcourse.web2_1_dashbunny_be.feature.owner.common.Validator;
import com.devcourse.web2_1_dashbunny_be.feature.owner.menu.repository.MenuRepository;
import com.devcourse.web2_1_dashbunny_be.feature.order.repository.OrdersRepository;
import com.devcourse.web2_1_dashbunny_be.feature.owner.store.repository.StoreFeedBackRepository;
import com.devcourse.web2_1_dashbunny_be.feature.owner.store.repository.StoreManagementRepository;
import com.order.generated.ordersListResponseProtobuf;
import com.devcourse.web2_1_dashbunny_be.feature.user.repository.UserRepository;
import com.order.generated.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.text.DecimalFormat;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;


@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

  private final Validator validator;
  private final OrdersRepository ordersRepository;
  private final MenuRepository menuRepository;
  private final SimpMessagingTemplate messageTemplate;
  private final MenuCacheService menuCacheService;
  private static final String ERROR_TOPIC = "/topic/order/error";
  private final UserRepository userRepository;
  private final StoreManagementRepository storeManagementRepository;
  private final StoreFeedBackRepository storeFeedBackRepository;
  private final OrderCacheService orderCacheService;

  /**
   * 사용자의 주문 요청을 처리합니다.
   * Async 어노테이션을 사용 하지 않고 .supplyAsync 사용하여 명시적으로 비동기 처리를 진행하였습니다.
   * 재고 등록 여부에 따라 각각 비동기 처리를 하였습니다.
   */
  @Transactional
  @Override
  public CompletableFuture<Orders> creatOrder(OrderInfoRequestDto orderInfoRequestDto) {
    return CompletableFuture.supplyAsync(() -> {
      User user = validator.validateUserId(orderInfoRequestDto.getUserPhone());
      StoreManagement store = validator.validateStoreId(orderInfoRequestDto.getStoreId());
      Orders orders = orderInfoRequestDto.toEntity(orderInfoRequestDto.getOrderItems(), menuRepository, user, store);

      log.info(orders.getDeliveryAddress());
      //재고 등록이 된 메뉴 리스트
      List<OrderItem> stockItems = filterStockItems(orders.getOrderItems(),true);
      Map<Long, MenuManagement> stockItemsMenuCache = getMenuCache(stockItems);
      List<OrderItem> nonStockItems = filterStockItems(orders.getOrderItems(),false);

      processStockItems(stockItems, stockItemsMenuCache,nonStockItems);

      orders.setTotalMenuCount(stockItems.size() + nonStockItems.size());
      // 주문 저장
      ordersRepository.save(orders);
      //레디스 캐시 저장
      saveOrderToRedis(orders, orders.getStore().getStoreId());
      // 메시지 알림
      StoreOrderAlarmResponseDto responseDto = StoreOrderAlarmResponseDto.fromEntity(orders);
      // 메시지 페이로드 구성
      Map<String, Object> payload = new HashMap<>();
      payload.put("type", "ORDER_RECEIVED"); // 이벤트 타입
      payload.put("message", "새로운 주문이 접수되었습니다.");
      payload.put("data", responseDto); // DTO를 데이터에 포함

      String orderTopic = String.format("/topic/storeOrder/" + orders.getOrderId());
      messageTemplate.convertAndSend(orderTopic, responseDto);
      log.info("사장님 알람 전송" + payload);

      return orders;
    });
  }

  /**
   * 주문 정보를 프로토버퍼를 사용하여 바이너리 형태로 변환 후 레디스 캐시에 저장합니다.
   * key : storeId
   * filed : orderId
   * value : Orders
   */
  private void saveOrderToRedis(Orders orders, String storeId){

    OrdersProtobuf.Orders order = OrdersProtobuf.Orders.newBuilder()
            .setOrderId(orders.getOrderId())
            .setUserId(orders.getUser().getUserId())
            .setStoreId(orders.getStore().getStoreId())
            .setDeliveryAddress(orders.getDeliveryAddress())
            .setDetailDeliveryAddress(orders.getDetailDeliveryAddress())
            .setStoreNote(orders.getStoreNote())
            .setRiderNote(orders.getRiderNote())
            .setDeliveryPrice(orders.getDeliveryPrice())
            .setTotalPrice(orders.getTotalPrice())
            .setTotalMenuCount(orders.getTotalMenuCount())
            .setOrderStatus(OrderStatusProtobuf.OrderStatus.PENDING)
            .setPreparationTime(orders.getPreparationTime())
            .addAllOrderItems(orders.getOrderItems().stream()
                    .map(this::OrderItemToProto) // OrderItem 변환
                    .collect(Collectors.toList())) // 리스트로 수집
            .build();
    // 직렬화
    orderCacheService.addOrderToStore(storeId, order.getOrderId(), order);
  }

  private OrderItemProtobuf.OrderItem OrderItemToProto(OrderItem orderItem) {
    return OrderItemProtobuf.OrderItem.newBuilder()
            .setMenuId(orderItem.getMenu().getMenuId())
            .setMenuName(orderItem.getMenu().getMenuName())
            .setStockAvailableAtOrder(orderItem.isStockAvailableAtOrder())
            .setQuantity(orderItem.getQuantity())
            .setPrice(orderItem.getTotalPrice())
            .build();
  }

  private void processStockItems(List<OrderItem> stockItems,
                                 Map<Long, MenuManagement> stockItemsMenuCache,
                                 List<OrderItem> nonStockItems) {
      try {
        if(!stockItems.isEmpty()) {
          log.info("재고 처리 중");
          soldOutCheck(stockItems, stockItemsMenuCache);
          updateMenuStock(stockItems, stockItemsMenuCache, 1);
        }
      } catch (Exception e) {
        log.error("재고 처리 중 예외 발생", e);
        throw new RuntimeException(e);
      }
  }


  private List<OrderItem> filterStockItems(List<OrderItem> orderItems, boolean stockAvailable) {
    return orderItems.stream()
            .filter(orderItem -> orderItem.isStockAvailableAtOrder() == stockAvailable)
            .toList();
  }

  /**
  * OrderItem 리스트를 기반으로 메뉴 ID를 키로,
  * 메뉴 객체(MenuManagement)를 값으로 하는 맵을 생성.
  *  메뉴 ID에 대해 중복으로 조회하지 않도록 캐싱 기능을 제공하기 위한 메서드.
  */
  public Map<Long, MenuManagement> getMenuCache(List<OrderItem> orderItems) {
    Map<Long, MenuManagement> menuCache = new HashMap<>();
    log.info(orderItems.toString());
    String storeKey = orderItems.get(0).getStoreId();
    List<Long> menuIds = orderItems.stream()
            .map(orderItem -> orderItem.getMenu().getMenuId())
            .toList();

    //레디스 캐시에 담아져있는 메뉴 가져와서 map 에 담기 없는 메뉴면 디비에서 가져오기
    try{
      for (Long menuId : menuIds) {
        MenuManagement menu = menuCacheService.getMenuFromStore(storeKey, menuId);

        if (menu == null) {
          menu = validator.validateMenuId(menuId);
        }

        if (menu != null) {
          menuCache.put(menu.getMenuId(), menu);
        }
      }
    } catch (Exception e) {

      e.printStackTrace();
    }

    return menuCache;
  }


  /**
   * 재고 확인 메서드.
   * 사용자가 주문한 메뉴의 수량과 기존 메뉴의 수량을 비교합니다.
   * 이때 사용자가 주문한 메뉴의 수량이 재고보다 크거나 같을때 true를 반환하고 아닐경우에 에러를 던집니다.
   */
  public void soldOutCheck(List<OrderItem> orderItems, Map<Long, MenuManagement> menuCache) {
    boolean menuStock = orderItems.stream().allMatch(orderItem -> {
      MenuManagement menu = menuCache.get(orderItem.getMenu().getMenuId());
      if (menu == null) {
        log.error("재고 확인 중 메뉴 정보를 찾을 수 없습니다. 메뉴 ID: {}", orderItem.getMenu().getMenuId());
        throw new IllegalArgumentException("메뉴 정보를 찾을 수 없습니다.");
      }
      log.info("재고가 충분합니다. 메뉴 ID: {}, 남은 재고: {}", menu.getMenuId(), menu.getMenuStock());
      return menu.getMenuStock() >= orderItem.getQuantity();
    });
    if (!menuStock) {
      throw new IllegalStateException("재고가 부족합니다.");
    }
  }

  /**
  * 재고 확인이 된 메뉴에 한에서 db에 재고를 업데이트 합니다.
  */
  public void updateMenuStock(List<OrderItem> orderItems, Map<Long, MenuManagement> menuCache, int type) {
    orderItems.forEach(orderItem -> {
      MenuManagement menu = menuCache.get(orderItem.getMenu().getMenuId());

  if(type == 1) {
        menu.setMenuStock(menu.getMenuStock() - orderItem.getQuantity());
        log.info("재고 업데이트 진행"+menu.getMenuStock());
  }
  else if (type == 0) {
  menu.setMenuStock(menu.getMenuStock() + orderItem.getQuantity());
  }
      log.info("재고 업데이트 진행"+menu.getMenuStock());
  menuRepository.save(menu);
    });
    //레디스 변경
  }

  @Async
  @Transactional
  @Override
  public CompletableFuture<AcceptOrdersResponseDto> acceptOrder(OrderAcceptRequestDto acceptRequestDto) {
    Orders orders = validator.validateOrderId(acceptRequestDto.getOrderId());
    orders.setOrderStatus(OrderStatus.IN_PROGRESS);
    orders.setPreparationTime(acceptRequestDto.getPreparationTime());
    ordersRepository.save(orders);
    AcceptOrdersResponseDto responseDto = AcceptOrdersResponseDto.fromEntity(orders);
    return CompletableFuture.completedFuture(responseDto);
  }

  @Async
  @Transactional
  @Override
  public CompletableFuture<DeclineOrdersResponseDto> declineOrder(OrderDeclineRequestDto declineRequestDto) {
    Orders orders = validator.validateOrderId(declineRequestDto.getOrderId());
    //주문 취소의 경우 재고 돌려두기// 재고 등록 여부가 true인 사항에 한해서만c
    List<OrderItem> stockItems = orders.getOrderItems().stream()
            .filter(orderItem -> orderItem.getMenu().isStockAvailable())
            .toList();

    Map<Long, MenuManagement> menuCache = getMenuCache(stockItems);
    updateMenuStock(stockItems, menuCache, 0);

    orders.setOrderStatus(OrderStatus.DECLINED);
    ordersRepository.save(orders);

    DeclineOrdersResponseDto responseDto = DeclineOrdersResponseDto
            .fromEntity(orders, declineRequestDto.getDeclineReasonType());

    return CompletableFuture.completedFuture(responseDto);
  }

  @Override
  public ordersListResponseProtobuf.OrdersListResponse getOrdersList(String storeId) {
    // 1. Store ID로 모든 Orders 가져오기
    List<Orders> orders = ordersRepository.findAllByStore_StoreId(storeId);

    // 2. Orders → OrderDetail Protobuf 메시지로 변환
    List<OrderDetailProtobuf.OrderDetail> orderDetails = orders.stream()
            .map(order -> {
              // OrderItems를 Protobuf로 변환
              List<OrderItemProtobuf.OrderItem> orderItems = order.getOrderItems().stream()
                      .map(orderItem -> OrderItemProtobuf.OrderItem.newBuilder()
                              .setMenuId(orderItem.getMenu().getMenuId())
                              .setMenuName(orderItem.getMenu().getMenuName())
                              .setStockAvailableAtOrder(orderItem.isStockAvailableAtOrder())
                              .setQuantity(orderItem.getQuantity())
                              .setPrice(orderItem.getTotalPrice())
                              .build())
                      .toList();
              //.newBuilder() → 빌더 객체 반환 (중간 단계).

              // Order → OrderDetail Protobuf로 변환
              return OrderDetailProtobuf.OrderDetail.newBuilder()
                      .setOrderId(order.getOrderId())
                      .setTotalPrice(order.getTotalPrice())
                      .addAllOrderItems(orderItems)
                      .setOrderStatus(OrderStatusProtobuf.OrderStatus.valueOf(order.getOrderStatus().name())) // Enum 매핑
                      .setStoreNote(order.getStoreNote())
                      .setPreparationTime(order.getPreparationTime())
                      .build();
            })
            .toList();

    // 3. 스토어가 가진 모든 Orders 정보를 OrderList Protobuf로 변환
    List<Orders> ordersList = validator.findByOrders(storeId);
    List<OrderListProtobuf.OrderList> orderLists = ordersList.stream()
            .map(order -> OrderListProtobuf.OrderList.newBuilder()
                    .addAllMenuName(order.getOrderItems().stream()
                            .map(orderItem -> orderItem.getMenu().getMenuName())
                            .toList())
                    .setPreparationTime(order.getPreparationTime())
                    .setTotalPrice(order.getTotalPrice())
                    .build())
            .toList();

    // 4. OrdersListResponse Protobuf 반환
    return ordersListResponseProtobuf.OrdersListResponse.newBuilder()
            .addAllOrderDetail(orderDetails)
            .addAllOrderList(orderLists)
            .build();
  }

    @Override
    public List<UserOrderInfoRequestDto> getUserOrderInfoList(String userId) {
        // 사용자 조회
        log.info(userId);
        User user = userRepository.findByPhone(userId).orElseThrow(() ->
                new IllegalArgumentException("해당 사용자를 찾을 수 없습니다.")
        );
        log.info("why not?");
        // 사용자의 주문 조회
        List<Orders> ordersList = ordersRepository.findByUser(user);
        log.info("please");
        // Orders -> UserOrderInfoRequestDto 변환 및 정렬
        List<UserOrderInfoRequestDto> userOrderInfoRequestDtos = ordersList.stream()
                .map(order -> {
                    // 주문 아이템 정보 추출
                    int totalQuantity = order.getOrderItems().stream()
                            .mapToInt(OrderItem::getQuantity) // OrderItem에서 수량 추출
                            .sum();

                    String menuName = order.getOrderItems().stream()
                            .map(orderItem -> orderItem.getMenu().getMenuName()) // OrderItem에서 메뉴 이름 추출
                            .findFirst() // 첫 번째 메뉴 이름 가져오기
                            .orElse("메뉴 이름 없음");

                    // DTO 변환
                    return UserOrderInfoRequestDto.todo(order, storeManagementRepository, totalQuantity, menuName);
                })
                .sorted((dto1, dto2) -> dto2.getOrderDate().compareTo(dto1.getOrderDate())) // orderDate 기준 내림차순 정렬
                .toList();
        log.info(userOrderInfoRequestDtos.toString());
        return userOrderInfoRequestDtos;
    }
    @Override
    public void increaseRating(OrderRatingResponseDto orders) {
        DecimalFormat df = new DecimalFormat("#.0");
        StoreFeedBack storeFeedBack = storeFeedBackRepository.findByStoreId(orders.getStoreId());
        storeFeedBack.increaseReviewCount();
        storeFeedBack.setTotalRating(storeFeedBack.getTotalRating() + orders.getRating());
        storeFeedBack.setRating(Double.valueOf(df.format(storeFeedBack.getTotalRating() / storeFeedBack.getReviewCount())));
        storeFeedBackRepository.save(storeFeedBack);
    }
}
