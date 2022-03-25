package com.engwili.arfe.engine;

import com.engwili.arfe.entity.ScrappingLocation;
import com.engwili.arfe.entity.VisitedLocation;
import com.engwili.arfe.repository.ScrappingLocationRepository;
import com.engwili.arfe.repository.VisitedLocationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.context.ActiveProfiles;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@DataJpaTest
@ActiveProfiles("test")
class PosgressLocationSavingTest {

    @Autowired
    private TestEntityManager testEntityManager;

    @SpyBean
    private PosgressLocationSaving posgressLocationSaving;

    @Autowired
    private VisitedLocationRepository visitedLocationRepository;

    @Autowired
    private ScrappingLocationRepository scrappingLocationRepository;

    @Test
    void given_2AlreadyVisitedLocation_when_saveVisitedLocation_then_OneAdditionalVisitedLocationIsSaved() {

        var scrappingLocation0 = ScrappingLocation.builder()
                .url("test_url_0")
                .nickname("test_url_0")
                .maxArticleScraped(1)
                .build();

        var scrappingLocation1 = ScrappingLocation.builder()
                .url("test_url_1")
                .nickname("test_url_1")
                .maxArticleScraped(1)
                .build();
        var scrappingLocation2 = ScrappingLocation.builder()
                .url("test_url_2")
                .nickname("test_url_2")
                .maxArticleScraped(2)
                .build();

        var visitedLocation1 = new VisitedLocation();
        visitedLocation1 = visitedLocation1
                .scrappingLocation(scrappingLocation1)
                .visited(true)
                .accessedAt(LocalDate.now());

        var visitedLocation2 = new VisitedLocation();
        visitedLocation2 = visitedLocation2
                .scrappingLocation(scrappingLocation2)
                .visited(true)
                .accessedAt(LocalDate.now());

        scrappingLocation0 = testEntityManager.persist(scrappingLocation0);
        scrappingLocation1 = testEntityManager.persist(scrappingLocation1);
        scrappingLocation2 = testEntityManager.persist(scrappingLocation2);
        testEntityManager.persist(visitedLocation1);
        testEntityManager.persist(visitedLocation2);

        Assertions.assertThat(visitedLocationRepository.findByVisitedAndAccessedAt(true, LocalDate.now())).hasSize(2);

        posgressLocationSaving.saveVisitedLocation(List.of(scrappingLocation0, scrappingLocation1, scrappingLocation2));

        List<VisitedLocation> byVisitedAndDateAfter = visitedLocationRepository.findByVisitedAndAccessedAt(true, LocalDate.now());

        Assertions.assertThat(byVisitedAndDateAfter.size()).isEqualTo(3);

        var savedScrappedLocation = byVisitedAndDateAfter
                .stream()
                .map(VisitedLocation::scrappingLocation)
                .collect(Collectors.toList());

        Assertions.assertThat(savedScrappedLocation.contains(scrappingLocation0)).isTrue();
    }

}