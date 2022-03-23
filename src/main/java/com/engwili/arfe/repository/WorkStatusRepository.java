package com.engwili.arfe.repository;

import com.engwili.arfe.entity.WorkStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface WorkStatusRepository extends JpaRepository<WorkStatus, Long> {

    List<WorkStatus> findByWorkId(String workId);

    List<WorkStatus> findByTriggeredAtBefore(Instant localDate);
}
