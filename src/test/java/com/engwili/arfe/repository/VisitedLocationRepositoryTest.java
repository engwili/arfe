package com.engwili.arfe.repository;

import com.engwili.arfe.entity.ScrappingLocation;
import com.engwili.arfe.entity.VisitedLocation;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;


@DataJpaTest
@ActiveProfiles(profiles = "test")
class VisitedLocationRepositoryTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @Autowired
    private VisitedLocationRepository visitedLocationRepository;

    @Autowired
    private ScrappingLocationRepository scrappingLocationRepository;

    @Test
    void given_noVisitedLocationForToday_when_retrievingAllVisitedLocations_then_returnZeroLocations() {

        ScrappingLocation scrappingLocation = ScrappingLocation.builder()
                .maxArticleScraped(2)
                .nickname("arxiv-ai")
                .frequency("day")
                .url("http://localhost.com")
                .build();

        var savedScrappingLocation = scrappingLocationRepository.save(scrappingLocation);

        var yesterday = LocalDate.now().minusDays(1);

        VisitedLocation visitedLocation = new VisitedLocation();
        visitedLocation = visitedLocation
                .visited(true)
                .scrappingLocation(savedScrappingLocation)
                .accessedAt(yesterday);

        testEntityManager.persist(visitedLocation);

        var result = visitedLocationRepository.findByVisitedAndAccessedAt(true, LocalDate.now());

        Assertions.assertThat(result).isEmpty();
    }

    @Test
    void given_oneVisitedLocationForToday_when_retrievingAllVisitedLocations_then_returnOneLocation() {

        ScrappingLocation scrappingLocation = ScrappingLocation.builder()
                .maxArticleScraped(2)
                .nickname("arxiv-ai")
                .url("http://localhost.com")
                .build();

        var savedScrappingLocation = testEntityManager.persist(scrappingLocation);

        var today = LocalDate.now();

        VisitedLocation visitedLocation = new VisitedLocation();
        visitedLocation = visitedLocation
                .visited(true)
                .scrappingLocation(savedScrappingLocation)
                .accessedAt(today);

        testEntityManager.persist(visitedLocation);

        var result = visitedLocationRepository.findByVisitedAndAccessedAt(true, LocalDate.now());

        Assertions.assertThat(result).isNotEmpty();
    }

}