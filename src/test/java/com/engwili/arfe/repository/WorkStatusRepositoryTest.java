package com.engwili.arfe.repository;

import com.engwili.arfe.entity.Status;
import com.engwili.arfe.entity.WorkStatus;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.Instant;
import java.time.LocalDate;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class WorkStatusRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private WorkStatusRepository workStatusRepository;

    @Test
    void given_workStatus_when_retrieveWorkProof_then_workProofIsRetrieved() {
        var workId = UUID.randomUUID().toString();
        workStatusRepository.save(new WorkStatus(Instant.now(), Status.IN_PROGRESS, workId));

        var result = workStatusRepository.findByWorkId(workId);
        assertThat(!result.isEmpty()).isTrue();
        assertThat(result.get(0).getWorkId()).isEqualTo(workId);
    }

}