package com.devcourse.web2_1_dashbunny_be.feature.order.controller.dto;

import com.devcourse.web2_1_dashbunny_be.domain.owner.MenuManagement;
import com.devcourse.web2_1_dashbunny_be.domain.owner.StoreManagement;
import com.devcourse.web2_1_dashbunny_be.domain.user.OrderItem;
import com.devcourse.web2_1_dashbunny_be.domain.user.Orders;
import com.devcourse.web2_1_dashbunny_be.domain.user.User;
import com.devcourse.web2_1_dashbunny_be.feature.owner.menu.repository.MenuRepository;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

/**
 * OrderItemDto 를 기반으로 주문 요청을 위한 dto.
 */
@Getter
@Setter
@Builder
@ToString
public class OrderInfoRequestDto {

  private String storeId; // 가게 ID
  private String userPhone;
  private List<OrderItemDto> orderItems; // 주문 항목
  private LocalDateTime orderDate; // 주문 날짜
  private String deliveryAddress; // 배달 주소
  private String detailDeliveryAddress;
  private String storeNote; // 사장님에게 전달할 메모
  private String riderNote; // 라이더에게 전달할 메모
  private String paymentId;
  private Long deliveryPrice;
  private Long totalAmount;

  public Orders toEntity(List<OrderItemDto> orderItems, MenuRepository menuRepository, User user, StoreManagement store) {

    List<OrderItem> orderItemList = orderItems.stream()
            .map(orderItemDto -> {
              MenuManagement menu = menuRepository.findById(orderItemDto.getMenuId())
                      .orElseThrow(() -> new IllegalArgumentException());
              return orderItemDto.toEntity(menu);
            }).toList();

    Long calculatedTotalAmount = orderItemList.stream()
            .mapToLong(OrderItem::getTotalPrice)
            .sum();

    Orders orders = new Orders();
    orders.setStore(store);
    orders.setUser(user);
    orders.setOrderDate(this.orderDate);
    orders.setOrderItems(orderItemList);
    orders.setDeliveryAddress(this.deliveryAddress);
    orders.setDetailDeliveryAddress(this.detailDeliveryAddress);
    orders.setDeliveryPrice(this.deliveryPrice);
    orders.setStoreNote(this.storeNote);
    orders.setRiderNote(this.riderNote);
    orders.setPaymentId(this.paymentId);
    orders.setTotalPrice(calculatedTotalAmount);

    // 양방향 관계 설정
    orderItemList.forEach(orderItem -> orderItem.setOrder(orders));

    return orders;
  }
}
//클래스 리펙토링 시급