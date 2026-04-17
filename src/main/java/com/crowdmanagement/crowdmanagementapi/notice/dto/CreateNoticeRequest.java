package com.crowdmanagement.crowdmanagementapi.notice.dto;

import com.crowdmanagement.crowdmanagementapi.notice.NoticeType;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class CreateNoticeRequest {
    private String roomName;
    private String buildingId;
    private NoticeType type;
    private String message;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
}
