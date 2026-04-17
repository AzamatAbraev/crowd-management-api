package com.crowdmanagement.crowdmanagementapi.notice;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RoomNoticeRepository extends JpaRepository<RoomNotice, String> {
    List<RoomNotice> findByActiveTrueOrderByCreatedAtDesc();
    List<RoomNotice> findAllByOrderByCreatedAtDesc();
}
