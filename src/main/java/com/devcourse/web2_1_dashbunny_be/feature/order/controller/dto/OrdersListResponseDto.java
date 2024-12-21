package com.devcourse.web2_1_dashbunny_be.feature.order.controller.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.util.List;

@Getter
@Setter
@Builder
public class OrdersListResponseDto implements Serializable {
    private List<OrderDetailDto> orderDetail;
    private List<OrderListDto>orderList;

    public OrdersListResponseDto(List<OrderDetailDto> orderDetail, List<OrderListDto> orderList) {
        this.orderDetail = orderDetail;
        this.orderList = orderList;
    }
}
