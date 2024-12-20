package com.devcourse.web2_1_dashbunny_be.feature.admin.notice.controller;


import com.devcourse.web2_1_dashbunny_be.domain.admin.Notice;
import com.devcourse.web2_1_dashbunny_be.feature.admin.notice.dto.AdminAddNoticeRequestDto;
import com.devcourse.web2_1_dashbunny_be.feature.admin.notice.dto.AdminNoticeListResponseDto;
import com.devcourse.web2_1_dashbunny_be.feature.admin.notice.dto.AdminNoticeResponseDto;
import com.devcourse.web2_1_dashbunny_be.feature.admin.notice.dto.AdminUpdateNoticeRequestDto;
import com.devcourse.web2_1_dashbunny_be.feature.admin.notice.service.NoticeService;

import java.security.Principal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.devcourse.web2_1_dashbunny_be.feature.user.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;


/**
 * 공지사항 controller.
 */
@RestController
@RequiredArgsConstructor
@Slf4j
@RequestMapping("/api/notice")
public class NoticeController {
  private final NoticeService noticeService;
  private final UserService userService;

  /**
   * 공지사항 등록 api (POST).
   */
  @PostMapping("/admin")
  public ResponseEntity<Notice> addNotice(@RequestBody AdminAddNoticeRequestDto request
          , @RequestHeader("Authorization") String authorizationHeader) {
    String phone = userService.getCurrentUser(authorizationHeader).getPhone();

    log.info("------------currentUser: "+phone);

    Notice saveNotice = noticeService.saveNotice(request);
    return ResponseEntity.status(HttpStatus.CREATED).body(saveNotice);
  }

  //@PreAuthorize("hasRole('ROLE_ADMIN') or #role == authentication.principal.role")
  /**
   * 공지사항 목록 조회 api (GET)
   * 현재 로그인한 사용자의 권한을 내부에서 확인하고 필터링해야함.
   */
  //http://localhost:8080/api/notice/admin?role=OWNER이게 아님..
  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<List<AdminNoticeListResponseDto>> getNotices(   @RequestHeader("Authorization") String authorizationHeader)  {
    String phone = userService.getCurrentUser(authorizationHeader).getPhone();

    log.info("------------currentUser: " + phone);

    String currentUserRole = noticeService.getCurrentUserRole(phone);
    log.info("------------currentUserRole: " + currentUserRole);

    List<AdminNoticeListResponseDto> notices;

    if (!currentUserRole.equals("ROLE_ADMIN")) { //사장님, 사용자가 조회
      notices = noticeService.getAllNoticesByRole(currentUserRole);
    } else { //관리자 조회
      notices = noticeService.getAllNotices();
    }
    return ResponseEntity.ok().body(notices);
  }


  /**
   *단일 공지사항 조회 api (GET).
   */
  @GetMapping(value = "/id/{noticeId}",produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<AdminNoticeResponseDto> getNotice(@PathVariable Long noticeId
          , @RequestHeader("Authorization") String authorizationHeader) {
    String phone = userService.getCurrentUser(authorizationHeader).getPhone();
    AdminNoticeResponseDto adminNoticeResponseDto = noticeService.getNotice(noticeId);
    return ResponseEntity.ok().body(adminNoticeResponseDto);
  }

  /**
   *공지사항 삭제 api (DELETE).
   */
  @DeleteMapping("/admin/{noticeId}")
  public ResponseEntity<?> deleteNotice(@PathVariable Long noticeId
          , @RequestHeader("Authorization") String authorizationHeader) {
    String phone = userService.getCurrentUser(authorizationHeader).getPhone();

    noticeService.deleteNotice(noticeId);
    Map<String, String> response = new HashMap<>();
    response.put("message", "NoticeId: " + noticeId + " deleted successfully");
    return ResponseEntity.status(HttpStatus.OK).body(response);
  }

  /**
   * 공지사항 수정 api (PUT).
   */
  @PutMapping("/admin/{noticeId}")
  public ResponseEntity<AdminUpdateNoticeRequestDto> updateNotice(@PathVariable Long noticeId, @RequestBody AdminAddNoticeRequestDto request
          , @RequestHeader("Authorization") String authorizationHeader) {
    String phone = userService.getCurrentUser(authorizationHeader).getPhone();
    AdminUpdateNoticeRequestDto adminUpdateNoticeRequestDto = noticeService.updateNotice(noticeId, request);
    return ResponseEntity.ok().body(adminUpdateNoticeRequestDto);
  }
}
