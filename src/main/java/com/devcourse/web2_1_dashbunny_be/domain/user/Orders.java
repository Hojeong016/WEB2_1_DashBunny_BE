package com.devcourse.web2_1_dashbunny_be.domain.user;

import com.devcourse.web2_1_dashbunny_be.domain.owner.StoreManagement;
import com.devcourse.web2_1_dashbunny_be.domain.user.role.OrderStatus;
import jakarta.persistence.*;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

/**
 * 주문내역 entity.
 * 아직 연관관계 설정이 안됨
 */
@Getter
@Setter
@Entity
@ToString
public class Orders implements Serializable {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "order_id")
  private Long orderId;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "user_id", nullable = false)
  private User user;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "store_id", nullable = false)
  private StoreManagement store;

  @OneToMany(mappedBy = "order", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<OrderItem> orderItems;

  private String couponDetails;

  private String deliveryAddress;

  private String detailDeliveryAddress;

  private String storeNote;

  private String riderNote;

  private LocalDateTime orderDate;

  private LocalDateTime createdAt;

  private Long deliveryPrice;

  private String paymentId;

  private Double rating;

  @Column(nullable = false)
  private Long totalPrice; // 총 금액 (단가 * 수량)

  private int totalMenuCount;

  private OrderStatus orderStatus = OrderStatus.PENDING;

  private int preparationTime;
}
