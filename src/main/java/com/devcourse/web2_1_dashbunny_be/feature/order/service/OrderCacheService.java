package com.devcourse.web2_1_dashbunny_be.feature.order.service;

import com.order.generated.OrdersProtobuf;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;
import java.util.HashMap;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class OrderCacheService {

    @Qualifier("orderRedisTemplate")
    private final HashOperations<String, String, OrdersProtobuf.Orders> hashOps;

    /**
     * 가게의 주문 정보를 레디스 캐시에 저장합니다.
     * 주문 정보 filed 타입 : Long
     * 주문 정보 value 타입 : OrdersProtobuf (바이너리)로 저장 됩니다.
     */
    public void addOrderToStore(String storeId, Long orderId, OrdersProtobuf.Orders order) {
        String key = "store:" + storeId;
        String filed = " orderId:" + orderId;

        hashOps.put(key, filed, order);
    }

    /**
     * key(storeId)에 따른 모든 주문 내역 정보를 반환합니다.
     * return = filed, value
     * type = Long,  OrdersProtobuf.Orders(바이너리)
     */
    public Map<Long,OrdersProtobuf.Orders> getOrderFromStore(String storeId) {
        String key = "store:" + storeId;
        Map<String, OrdersProtobuf.Orders> orderListMap = hashOps.entries(key);
        log.info(orderListMap.keySet().toString());

        Map<Long,OrdersProtobuf.Orders> ordersList = new HashMap<>();
        orderListMap.forEach((f,v)->{
            if (f.startsWith(" orderId:")){
                Long orderId = Long.parseLong(f.substring(" orderId:".length()));
                ordersList.put(orderId, v);

            }
            log.info(String.valueOf(ordersList.size()));
        });

        return ordersList;
    }

}
