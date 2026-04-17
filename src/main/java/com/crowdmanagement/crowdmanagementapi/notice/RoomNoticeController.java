package com.crowdmanagement.crowdmanagementapi.notice;

import com.crowdmanagement.crowdmanagementapi.notice.dto.CreateNoticeRequest;
import com.crowdmanagement.crowdmanagementapi.utils.ApiResponse;
import com.crowdmanagement.crowdmanagementapi.utils.ResponseBuilder;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/notices")
@RequiredArgsConstructor
public class RoomNoticeController {

    private final RoomNoticeService noticeService;

    @GetMapping
    public ResponseEntity<ApiResponse> getNotices(
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {
        List<RoomNotice> notices = activeOnly
                ? noticeService.getActiveNotices()
                : noticeService.getAllNotices();
        return ResponseBuilder.build(HttpStatus.OK, "success", notices);
    }

    @PostMapping
    @PreAuthorize("hasAnyRole('facility_manager', 'system_admin', 'theking')")
    public ResponseEntity<ApiResponse> createNotice(
            @RequestBody CreateNoticeRequest request,
            @AuthenticationPrincipal Jwt jwt) {
        String createdBy = jwt.getClaimAsString("preferred_username");
        RoomNotice notice = noticeService.createNotice(request, createdBy);
        return ResponseBuilder.build(HttpStatus.CREATED, "Notice created", notice);
    }

    @PatchMapping("/{id}/resolve")
    @PreAuthorize("hasAnyRole('facility_manager', 'system_admin', 'theking')")
    public ResponseEntity<ApiResponse> resolveNotice(@PathVariable String id) {
        return noticeService.resolveNotice(id)
                .map(n -> ResponseBuilder.build(HttpStatus.OK, "Notice resolved", n))
                .orElse(ResponseBuilder.build(HttpStatus.NOT_FOUND, "Notice not found", null));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('facility_manager', 'system_admin', 'theking')")
    public ResponseEntity<Void> deleteNotice(@PathVariable String id) {
        noticeService.deleteNotice(id);
        return ResponseEntity.noContent().build();
    }
}
