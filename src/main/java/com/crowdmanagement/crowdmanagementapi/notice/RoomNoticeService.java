package com.crowdmanagement.crowdmanagementapi.notice;

import com.crowdmanagement.crowdmanagementapi.notice.dto.CreateNoticeRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class RoomNoticeService {

    private final RoomNoticeRepository repository;

    public RoomNotice createNotice(CreateNoticeRequest request, String createdBy) {
        RoomNotice notice = RoomNotice.builder()
                .roomName(request.getRoomName())
                .buildingId(request.getBuildingId())
                .type(request.getType())
                .message(request.getMessage())
                .startTime(request.getStartTime())
                .endTime(request.getEndTime())
                .createdBy(createdBy)
                .active(true)
                .build();
        return repository.save(notice);
    }

    public List<RoomNotice> getAllNotices() {
        return repository.findAllByOrderByCreatedAtDesc();
    }

    public List<RoomNotice> getActiveNotices() {
        return repository.findByActiveTrueOrderByCreatedAtDesc();
    }

    public Optional<RoomNotice> resolveNotice(String id) {
        return repository.findById(id).map(notice -> {
            notice.setActive(false);
            return repository.save(notice);
        });
    }

    public void deleteNotice(String id) {
        repository.deleteById(id);
    }
}
