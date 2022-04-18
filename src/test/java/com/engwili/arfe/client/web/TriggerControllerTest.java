package com.engwili.arfe.client.web;

import com.engwili.arfe.entity.ScrappingLocation;
import com.engwili.arfe.repository.ScrappingLocationRepository;
import com.engwili.arfe.repository.WorkStatusRepository;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
class TriggerControllerTest {

    @Autowired
    private TriggerController triggerController;

    @Autowired
    private ScrappingLocationRepository scrappingLocationRepository;

    @Autowired
    private WorkStatusRepository workStatusRepository;

    @DisplayName("when scrap location then return proof of work with status in progress")
    @Test
    void triggerScrapping() {
        var scrappingLocation = ScrappingLocation.builder()
                .maxArticleScraped(2)
                .url("https://arxiv.org/list/cs.DS/recent")
                .frequency("1-d")
                .nickname("arxiv-DS")
                .build();

        scrappingLocation = scrappingLocationRepository.save(scrappingLocation);

        var result = triggerController.triggerScrapping();

        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        var proof = (Objects.requireNonNull(result.getBody()).getWorkId());

        assertThat(workStatusRepository.findByWorkId(proof)).isNotEmpty();
    }

    @DisplayName("when scrap location then return the same proof of work for repeated calls")
    @Test
    void triggerScrappingRepeatedTrigger() throws InterruptedException {
        var scrappingLocation = ScrappingLocation.builder()
                .maxArticleScraped(2)
                .url("https://arxiv.org/list/cs.DS/recent")
                .frequency("1-d")
                .nickname("arxiv-DS")
                .build();

        scrappingLocation = scrappingLocationRepository.save(scrappingLocation);

        var result = triggerController.triggerScrapping();

        Thread.sleep(1000);

        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();

        var result2 = triggerController.triggerScrapping();

        assertThat(result).isEqualTo(result2);
    }

    @DisplayName("when scrap location and have 1 unvisited location then return different proof of work for repeated calls")
    @Test
    void triggerScrappingRepeatedTriggerDifferentProofForUnvisited() {
        var scrappingLocation = ScrappingLocation.builder()
                .maxArticleScraped(2)
                .url("https://arxiv.org/list/cs.DS/recent")
                .nickname("arxiv-DS")
                .frequency("1-d")
                .build();

        var unpScrappedLocation = ScrappingLocation.builder()
                .maxArticleScraped(2)
                .url("https://arxiv.org/list/cs.AI/recent")
                .nickname("arxiv-AI")
                .frequency("1-d")
                .build();

        scrappingLocation = scrappingLocationRepository.save(scrappingLocation);

        var result = triggerController.triggerScrapping();

        assertThat(result.getStatusCode().is2xxSuccessful()).isTrue();
        unpScrappedLocation = scrappingLocationRepository.save(unpScrappedLocation);

        var result2 = triggerController.triggerScrapping();
        assertThat(result).isNotEqualTo(result2);
    }
}