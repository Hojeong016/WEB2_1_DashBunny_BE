package com.devcourse.web2_1_dashbunny_be.feature.order.service;

import com.google.protobuf.Message;
import com.order.generated.OrdersProtobuf;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class OrderCacheService {

    @Qualifier("orderRedisTemplate")
    private final HashOperations<String, String, OrdersProtobuf.Orders> hashOps;

    // 주문 내역 추가
    public void addOrderToStore(String storeId, Long orderId, OrdersProtobuf.Orders order) {
        String key = "store:" + storeId;
        String filed = " orderId:" + orderId;

        hashOps.put(key, filed, order);
    }

}
