package com.devcourse.web2_1_dashbunny_be.feature.admin.store.dto;


import com.devcourse.web2_1_dashbunny_be.domain.owner.StoreManagement;
import com.devcourse.web2_1_dashbunny_be.domain.owner.role.StoreStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;


@Getter
@Builder
public class StoreCreateRequestDTO {
    private String storeName;          // 가게 이름
    private String contactNumber;      // 가게 연락처
    private String address;            // 가게 위치
    private String description;        // 가게 소개
    private String storeRegistrationDocs; // 등록 서류
    private String storeLogo;          // 가게 매장 로고
    private String storeBannerImage;    // 가게 배너 이미지
    private StoreStatus storeStatus;
    private String userName; //사장님 이름-- StoreManagement엔티티에 아직 없음

    public StoreManagement toEntity() {
        StoreManagement storeManagement = new StoreManagement();
        storeManagement.setStoreName(this.storeName);
        storeManagement.setContactNumber(this.contactNumber);
        storeManagement.setAddress(this.address);
        storeManagement.setStoreDescription(this.description);
        storeManagement.setStoreRegistrationDocs(this.storeRegistrationDocs);
        storeManagement.setStoreLogo(this.storeLogo);
        storeManagement.setStoreBannerImage(this.storeBannerImage);
        storeManagement.setStoreStatus(StoreStatus.PENDING);
        return storeManagement;
    }

/*    public StoreManagement toEntity() {
        return StoreManagement.builder()
                .storeName(storeName)
                .contactNumber(contactNumber)
                .address(address)
                .description(description)
                .category1(category1)
                .category2(category2)
                .category3(category3)
                .storeRegistrationDocs(storeRegistrationDocs)
                .storeLogo(storeLogo)
                .storeBannerImage(storeBannerImage)
                .storeStatus(storeStatus)
                //사장님 이름
                .build();

    }*/
}