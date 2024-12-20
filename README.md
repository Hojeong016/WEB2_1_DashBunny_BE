# **Dash Bunny**



**Dash Bunny**는 친근한 배달원 캐릭터 "토끼"를 활용해, 빠르고 효율적인 딜리버리 서비스를 제공하는 프로젝트입니다. <br>
기존 배달 앱의 "뭐 먹을까?" 고민으로 인한 불편함을 해결하기 위해, 쇼츠 콘텐츠로 간편한 음식 추천을 제공하여 사용자 경험을 증진하는 것을 목표로 합니다.

---

## 📋 프로젝트 구조
```plaintext
           +------------------+       
           |      Client      |       
           +------------------+       
                   |                 
   +--------------------------------+       
   |          WebSocket / SSE       |       
   +--------------------------------+       
                   |                 
        +---------------------+          
        |     Spring Boot     |          
        +---------------------+          
                   |                 
      +---------------------+          
      | Protocol Buffers     |          
      +---------------------+          
                   |                 
+-------+   +------+         +-----------+
| Redis |---|Kafka |---------| Coupon API|
+-------+   +------+         +-----------+
                   |                 
       +---------------------+            
       |      RDB - MYSQL    |           
       +---------------------+            
                   |                 
        +---------------------+         
        | AMAZON S3 STORAGE  |          
        +---------------------+         
```
---
## 📌 주요 기능 및 특징

### **사용자 도메인**

| 기능                          | 설명                                                                                                                                                     |
|------------------------------------|------------------------------------------------------------------------------------------------------------------------------|
| 사용자 도메인                       | - 회원가입<br> - 로그인 <br> - 마이페이지 <br> - 가게 리스트 조회 <br> -찜 등록 / 조회<br>-장바구니<br>-결제<br> 
| 쇼츠 도메인                       |- 사장님의 쇼츠 등록<br> - 유저의 주소 3km 반경 내 위치한 가게의 쇼츠 가져오기 

### **배달 도메인**                       
| **기능**                          | **설명**                                                                                              |
|------------------------------------|------------------------------------------------------------------------------------------------------|
| **배달 도메인**                     |- 가게의 배달 요청 <br>- 배달원의 배달 요청 수락<br>- 배달원의 배달 완료<br>- 배달원의 오늘 및 선택한 날짜별  배달 내역<br>                     |           

### **사장님 / 주문 도메인**

| **기능**                          | **설명**                                                                                              |
|------------------------------------|------------------------------------------------------------------------------------------------------|
| **사장님 도메인**                     | - 가게 등록, 정보 조회 및 수정<br>- 메뉴 및 그룹 추가, 수정, 삭제<br>- 쿠폰 발행 및 삭제                     |                    |
| **주문 도메인**                     | - 주문 요청 생성 및 상세 정보 조회<br>- 사장님이 주문을 접수 및 처리하는 상태 업데이트 기능<br>- 실시간 주문 현황 조회하기 <br>- 주문 완료 후 사용자와 가게 간의 알림 연동  |

### 관리자/ 쿠폰 도메인
| 기능                          | 설명                                                                                              |
|------------------------------------|------------------------------------------------------------------------------------------------------|
| 관리자 도메인                     | - 가게 등록 승인/거절/재승인 및 가게 폐업 승인<br>- 가게 상태별 목록 조회 및 상세 조회<br>- 공지사항 등록/수정/삭제 및 권한별 조회                    |                    |
| 쿠폰 도메인                     | - 관리자 일반 쿠폰 생성/조회 및 쿠폰 상태 변경 <br>- 관리자 선착순 쿠폰 생성/조회 및 쿠폰 상태 변경  <br>- 사용자 일반, 선착순, 가게 쿠폰 다운로드 <br>- 사용자 일반 쿠폰 조회, 가게 쿠폰 조회, 선착순 쿠폰 조회 <br>- 사용자 쿠폰함 조회 <br>- 사용자 장바구니 쿠폰 조회 및 사용 |

---
## 📋 설계도
<details>
<summary>자세히보기</summary>

-작성

</details>

---

## 📚 기술 스택 (Tech Stack)

### Database
![MySQL](https://img.shields.io/badge/MySQL-4479A1?style=flat&logo=mysql&logoColor=white)
![Redis](https://img.shields.io/badge/Redis-DC382D?style=flat&logo=redis&logoColor=white)

### Core Development Stack
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-6DB33F?style=flat&logo=spring-boot&logoColor=white)
![Amazon S3](https://img.shields.io/badge/Amazon%20S3-569A31?style=flat&logo=amazon-s3&logoColor=white)
![IntelliJ IDEA](https://img.shields.io/badge/IntelliJ%20IDEA-000000?style=flat&logo=intellij-idea&logoColor=white)
![Java](https://img.shields.io/badge/Java-007396?style=flat&logo=java&logoColor=white)

### Event Streaming
![Apache Kafka](https://img.shields.io/badge/Apache%20Kafka-231F20?style=flat&logo=apache-kafka&logoColor=white)
![SSE](https://img.shields.io/badge/Spring%20SSE-6DB33F?style=flat&logo=java&logoColor=white)

### 외부 API
![Kakao Login](https://img.shields.io/badge/Kakao%20Login-FFCD00?style=flat&logo=kakao&logoColor=white)
![CoolSMS](https://img.shields.io/badge/CoolSMS-008ED2?style=flat&logo=twilio&logoColor=white)
![Toss Payments](https://img.shields.io/badge/Toss%20Payments-0054FF?style=flat&logo=tosspayments&logoColor=white)
![Kakao Map](https://img.shields.io/badge/Kakao%20Map-FFCD00?style=flat&logo=kakao&logoColor=white)
![Geocoding Library](https://img.shields.io/badge/Geocoding%20Library-4E73DF?style=flat&logo=google-maps&logoColor=white)

---



## 📌 팀원 소개

<table>
  <tbody>
    <!-- 첫 번째 행: 팀원 이름 -->
    <tr>
      <td align="center"><b>채호정</b></td>
      <td align="center"><b>최근태</b></td>
      <td align="center"><b>도승우</b></td>
      <td align="center"><b>한승희</b></td>
    </tr>
    <tr>
      <td align="center">
        <a href="https://github.com/Hojeong016">
          <img src="https://avatars.githubusercontent.com/Hojeong016" width="100px;" alt="채호정 프로필 사진"/>
        </a>
      </td>
      <td align="center">
        <a href="https://github.com/GeunTae-C">
           <img src="https://avatars.githubusercontent.com/GeunTae-C" width="100px;" alt="최근태 프로필 사진"/>
        </a>
      </td>
      <td align="center">
        <a href="https://github.com/MagongDo">
        <img src="https://avatars.githubusercontent.com/MagongDo" width="100px;" alt="도승우 프로필 사진"/>
        </a>
      </td>
      <td align="center">
        <a href="https://github.com/SeungHuiHan">
          <img src="https://avatars.githubusercontent.com/SeungHuiHan" width="100px;" alt="한승희 프로필 사진"/>
        </a>
      </td>
    </tr>
    <tr>
      <td align="center"><b>BE 팀장<br/>사장님 도메인<br>주문 도메인</b></td>
      <td align="center"><b>BE<br/>회원 및 로그인<br/>쇼츠<br/>배달원 도메인<br/></b></td>
      <td align="center"><b>BE<br/>시용자 도메인<br/>결제</b></td>
      <td align="center"><b>관리자 도메인<br/>쿠폰 도메인</td>
    </tr>
  </tbody>
</table>

---
## 주요 기능 에러 사항 및 진행 상황 

<details>
<summary>📋 더 자세히 보기</summary>

-작성

</details>

<details>
<summary>📋 더 자세히 보기</summary>

-작성

</details>

<details>
<summary>📋 더 자세히 보기</summary>

-작성

</details>

<details>
<summary>📋 더 자세히 보기</summary>

-작성

</details>







