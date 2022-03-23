package com.engwili.arfe.entity;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.ToString;

import javax.persistence.*;
import java.time.Instant;

@Getter
@NoArgsConstructor
@ToString
@Entity
public class WorkStatus {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 512)
    private Instant triggeredAt;

    @Enumerated(EnumType.STRING)
    private Status status;

    private String workId;

    public WorkStatus(Instant triggeredAt, Status status, String workId) {
        this.triggeredAt = triggeredAt;
        this.status = status;
        this.workId = workId;
    }
}