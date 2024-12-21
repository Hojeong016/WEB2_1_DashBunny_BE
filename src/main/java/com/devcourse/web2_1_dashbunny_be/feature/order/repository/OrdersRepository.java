package com.devcourse.web2_1_dashbunny_be.feature.order.repository;

import com.devcourse.web2_1_dashbunny_be.domain.user.Orders;
import com.devcourse.web2_1_dashbunny_be.domain.user.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

import java.util.List;

/**
 * 주문 내역 repository.
 */
@Repository
public interface OrdersRepository extends JpaRepository<Orders, Long> {

    // 모든 주문 조회
    List<Orders> findAllByStore_StoreId(String storeId);
    Orders findByPaymentId(String paymentId);
    List<Orders> findByUser(User user);
}
