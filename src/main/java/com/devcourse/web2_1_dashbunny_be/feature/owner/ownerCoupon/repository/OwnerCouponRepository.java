package com.devcourse.web2_1_dashbunny_be.feature.owner.ownerCoupon.repository;

import com.devcourse.web2_1_dashbunny_be.domain.owner.OwnerCoupon;
import com.devcourse.web2_1_dashbunny_be.domain.owner.role.CouponStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import java.time.LocalDateTime;
import java.util.List;

public interface OwnerCouponRepository extends JpaRepository<OwnerCoupon, Long> {

    List<OwnerCoupon> findByStoreManagement_StoreIdAndCouponStatus(String storeId, CouponStatus couponStatus);

    /**
     * 기한이 지난 쿠폰 중 아직 EXPIRED 상태가 아닌 쿠폰 조회 (현재 시간 기준).
     */
    List<OwnerCoupon> findByExpiredDateBeforeAndCouponStatusNot(LocalDateTime now, CouponStatus status);

}
