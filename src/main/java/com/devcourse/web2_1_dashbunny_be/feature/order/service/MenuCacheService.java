package com.devcourse.web2_1_dashbunny_be.feature.order.service;

import com.devcourse.web2_1_dashbunny_be.domain.owner.MenuManagement;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class MenuCacheService {

    @Qualifier("menuRedisTemplate")
    private final HashOperations<String, String, Object> hashOps;


    // 메뉴 추가
    public void addMenuToStore(String storeId, Long menuId, MenuManagement menu) {
        String key = "store:" + storeId; // Redis 키
        hashOps.put(key, menuId.toString(), menu); // 필드-값 저장
    }

    // 메뉴 조회
    public MenuManagement getMenuFromStore(String storeId, Long menuId) {
        String key = "store:" + storeId;
        return (MenuManagement) hashOps.get(key, menuId.toString());
    }

    // 메뉴 삭제
    public void removeMenuFromStore(String storeId, Long menuId) {
        String key = "store:" + storeId;
        hashOps.delete(key, menuId.toString());
    }

    // 가게의 모든 메뉴 조회
    public Map<Long, MenuManagement> getAllMenusFromStore(String storeId) {
        String key = "store:" + storeId;
        Map<String, Object> menuMap = hashOps.entries(key);

        // String 키를 Long으로 변환
        return menuMap.entrySet().stream()
                .collect(Collectors.toMap(
                        entry -> Long.parseLong(entry.getKey()),
                        entry -> (MenuManagement) entry.getValue()
                ));
    }

    // 가게의 모든 메뉴 조회
    public List<MenuManagement> getAllMenusFromStoreList(String storeId) {
        String key = "store:" + storeId;
        Map<String, Object> menuMap = hashOps.entries(key);

        // Map의 값들만 추출하여 List<MenuManagement>로 변환
        return menuMap.values().stream()
                .map(entry -> (MenuManagement) entry)
                .collect(Collectors.toList());
    }

    public MenuManagement getMenuFromCache(Long menuId, String storeId) {
        String pattern = storeId;
        String key = "store:" + storeId; // Redis 키
        return (MenuManagement) hashOps.get(key, menuId.toString()); // 메뉴 ID로 조회
    }
}
