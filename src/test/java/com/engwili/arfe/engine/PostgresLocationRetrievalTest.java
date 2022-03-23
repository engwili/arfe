package com.engwili.arfe.engine;

import com.engwili.arfe.entity.ScrappingLocation;
import com.engwili.arfe.entity.VisitedLocation;
import com.engwili.arfe.repository.ScrappingLocationRepository;
import com.engwili.arfe.repository.VisitedLocationRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@DataJpaTest
@ActiveProfiles("test")
@Transactional
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
class PostgresLocationRetrievalTest {

    @SpyBean
    private PostgresLocationRetrieval postgresLocationRetrieval;

    @Autowired
    private ScrappingLocationRepository scrappingLocationRepository;

    @Autowired
    private VisitedLocationRepository visitedLocationRepository;

    @Test
    void given_2AlreadyVisitedLocationAnd1ToBeVisited_when_retrieveUnvisitedScrappingLocations_then_OneToBeVisitedLocationIsReturned() {

        var scrappingLocation0 = ScrappingLocation.builder()
                .id(1L)
                .url("test_url_0")
                .frequency("1-d")
                .nickname("test_url_0")
                .maxArticleScraped(1)
                .build();

        var scrappingLocation1 = ScrappingLocation.builder()
                .id(2L)
                .url("test_url_1")
                .frequency("1-d")
                .nickname("test_url_1")
                .maxArticleScraped(1)
                .build();
        var scrappingLocation2 = ScrappingLocation.builder()
                .id(3L)
                .url("test_url_2")
                .frequency("1-d")
                .nickname("test_url_2")
                .maxArticleScraped(2)
                .build();

        scrappingLocation0 = scrappingLocationRepository.save(scrappingLocation0);
        scrappingLocation1 = scrappingLocationRepository.save(scrappingLocation1);
        scrappingLocation2 = scrappingLocationRepository.save(scrappingLocation2);

        var visitedLocation1 = new VisitedLocation();
        visitedLocation1 = visitedLocation1.scrappingLocation(scrappingLocation1).visited(true).accessedAt(LocalDate.now()).id(4L);

        var visitedLocation2 = new VisitedLocation();
        visitedLocation2 = visitedLocation2.scrappingLocation(scrappingLocation2).visited(true).accessedAt(LocalDate.now()).id(5L);

        visitedLocationRepository.saveAll(List.of(visitedLocation1, visitedLocation2));

        Assertions.assertThat(visitedLocationRepository.findAll()).hasSize(2);
        Assertions.assertThat(scrappingLocationRepository.findAll()).hasSize(3);

        var scrappingLocations = postgresLocationRetrieval.retrieveUnvisitedScrappingLocations();

        Assertions.assertThat(scrappingLocations.size()).isOne();
        Assertions.assertThat(scrappingLocations.get(0)).isEqualTo(scrappingLocation0);
    }

    @DisplayName("Given 2 already visited location and another location that is not ready to be visited no visits should occur")
    @Test
    void given_2AlreadyVisitedLocationAnd1NotReadyToBeVisited_when_retrieveUnvisitedScrappingLocations_then_noVisits() {

        var scrappingLocation0 = ScrappingLocation.builder()
                .id(1L)
                .url("test_url_0")
                .frequency("1-d")
                .nickname("test_url_0")
                .maxArticleScraped(1)
                .build();

        var scrappingLocation1 = ScrappingLocation.builder()
                .id(2L)
                .url("test_url_1")
                .frequency("1-d")
                .nickname("test_url_1")
                .maxArticleScraped(1)
                .build();
        var scrappingLocation2 = ScrappingLocation.builder()
                .id(3L)
                .url("test_url_2")
                .frequency("1-d")
                .nickname("test_url_2")
                .maxArticleScraped(2)
                .build();

        scrappingLocation0 = scrappingLocationRepository.save(scrappingLocation0);
        scrappingLocation1 = scrappingLocationRepository.save(scrappingLocation1);
        scrappingLocation2 = scrappingLocationRepository.save(scrappingLocation2);

        var visitedLocation1 = new VisitedLocation();
        visitedLocation1 = visitedLocation1.scrappingLocation(scrappingLocation1).visited(true).accessedAt(LocalDate.now()).id(4L);

        var visitedLocation2 = new VisitedLocation();
        visitedLocation2 = visitedLocation2.scrappingLocation(scrappingLocation2).visited(true).accessedAt(LocalDate.now()).id(5L);

        var visitedLocation3 = new VisitedLocation();
        visitedLocation3 = visitedLocation3.scrappingLocation(scrappingLocation0).visited(true).accessedAt(LocalDate.now()).id(6L);

        visitedLocationRepository.saveAll(List.of(visitedLocation1, visitedLocation2, visitedLocation3));

        Assertions.assertThat(visitedLocationRepository.findAll()).hasSize(3);
        Assertions.assertThat(scrappingLocationRepository.findAll()).hasSize(3);

        var scrappingLocations = postgresLocationRetrieval.retrieveUnvisitedScrappingLocations();

        Assertions.assertThat(scrappingLocations.size()).isZero();
    }

    @DisplayName("Given 3 already visited location and 1 location is ready to be revisited then 1 visit should occur")
    @Test
    void threeVisitedLocationAndOneToBeRevisited() {

        var scrappingLocation0 = ScrappingLocation.builder()
                .id(1L)
                .url("test_url_0")
                .frequency("1-d")
                .nickname("test_url_0")
                .maxArticleScraped(1)
                .build();

        var scrappingLocation1 = ScrappingLocation.builder()
                .id(2L)
                .url("test_url_1")
                .frequency("1-d")
                .nickname("test_url_1")
                .maxArticleScraped(1)
                .build();

        var scrappingLocation2 = ScrappingLocation.builder()
                .id(3L)
                .url("test_url_2")
                .frequency("1-d")
                .nickname("test_url_2")
                .maxArticleScraped(2)
                .build();

        scrappingLocation0 = scrappingLocationRepository.save(scrappingLocation0);
        scrappingLocation1 = scrappingLocationRepository.save(scrappingLocation1);
        scrappingLocation2 = scrappingLocationRepository.save(scrappingLocation2);

        var visitedLocation1 = new VisitedLocation();
        visitedLocation1 = visitedLocation1.scrappingLocation(scrappingLocation1).visited(true).accessedAt(LocalDate.now()).id(4L);

        var visitedLocation2 = new VisitedLocation();
        visitedLocation2 = visitedLocation2.scrappingLocation(scrappingLocation2).visited(true).accessedAt(LocalDate.now()).id(5L);

        var visitedLocation3 = new VisitedLocation();
        visitedLocation3 = visitedLocation3.scrappingLocation(scrappingLocation0).visited(true).accessedAt(LocalDate.now().minusDays(1)).id(6L);

        visitedLocationRepository.saveAll(List.of(visitedLocation1, visitedLocation2, visitedLocation3));

        Assertions.assertThat(visitedLocationRepository.findAll()).hasSize(3);
        Assertions.assertThat(scrappingLocationRepository.findAll()).hasSize(3);
        Assertions.assertThat(visitedLocationRepository.findByVisitedAndAccessedAt(true, LocalDate.now())).hasSize(2);

        var scrappingLocations = postgresLocationRetrieval.retrieveUnvisitedScrappingLocations();

        Assertions.assertThat(scrappingLocations.size()).isOne();
        Assertions.assertThat(scrappingLocations.get(0)).isEqualTo(scrappingLocation0);
    }

//    @ParameterizedTest
//    @MethodSource(value = "generateScrappingLocations")
//    void retrieveUnvisitedScrappingLocations(List<ScrappingLocation> scrappingLocations, List<VisitedLocation> visitedLocations, int expected) {
//
////        var scrappingLocation0 = ScrappingLocation.builder()
////                .id(1L)
////                .url("test_url_0")
////                .frequency("1-d")
////                .nickname("test_url_0")
////                .maxArticleScraped(1)
////                .build();
//////
////        var scrappingLocation1 = ScrappingLocation.builder()
////                .id(2L)
////                .url("test_url_1")
////                .frequency("1-d")
////                .nickname("test_url_1")
////                .maxArticleScraped(1)
////                .build();
////        var scrappingLocation2 = ScrappingLocation.builder()
////                .id(3L)
////                .url("test_url_2")
////                .frequency("1-d")
////                .nickname("test_url_2")
////                .maxArticleScraped(2)
////                .build();
//
////        var visitedLocation1 = new VisitedLocation();
////        visitedLocation1 = visitedLocation1.scrappingLocation(scrappingLocation1).visited(true).accessedAt(LocalDate.now()).id(4L);
////
////        var visitedLocation2 = new VisitedLocation();
////        visitedLocation2 = visitedLocation2.scrappingLocation(scrappingLocation2).visited(true).accessedAt(LocalDate.now()).id(5L);
////
////        var visitedLocation3 = new VisitedLocation();
////        visitedLocation3 = visitedLocation3.scrappingLocation(scrappingLocation0).visited(true).accessedAt(LocalDate.now().minusDays(1)).id(5L);
////
////        scrappingLocationRepository.saveAll(List.of(scrappingLocation0, scrappingLocation1, scrappingLocation2));
////        visitedLocationRepository.saveAll(List.of(visitedLocation1, visitedLocation2, visitedLocation3));
//
//        var saved = scrappingLocationRepository.saveAll(scrappingLocations);
//        var savedScrappingLocation = new ArrayList<ScrappingLocation>();
//        saved.forEach(savedScrappingLocation::add);
//
//        IntStream.range(0, visitedLocations.size())
//                .boxed()
//                .forEach(index -> visitedLocations.get(index).scrappingLocation(savedScrappingLocation.get(index)));
//
//        visitedLocationRepository.saveAll(visitedLocations);
//
////        Assertions.assertThat(visitedLocationRepository.findByVisitedAndAccessedAt(true, LocalDate.now())).hasSize(2);
////        Assertions.assertThat(scrappingLocationRepository.findAll()).hasSize(3);
//
//        var result = postgresLocationRetrieval.retrieveUnvisitedScrappingLocations();
//
//        Assertions.assertThat(result.size()).isEqualTo(expected);
//
////        Assertions.assertThat(result.size()).isOne();
////        Assertions.assertThat(result.get(0)).isEqualTo(scrappingLocation0);
//    }
//
//    public static Stream<Arguments> generateScrappingLocations() {
//        return Stream.of(
//                of(List.of(generateScrappingLocation(1L, "1-d"), generateScrappingLocation(2L, "2-d")),
//                        List.of(generateVisitedLocation(3L, 0), generateVisitedLocation(4L, 3)), 1),
//
//                of(List.of(generateScrappingLocation(1L, "1-m"), generateScrappingLocation(2L, "1-d")),
//                        List.of(generateVisitedLocation(3L, 0), generateVisitedLocation(4L, 0)), 1),
//
//                of(List.of(generateScrappingLocation(1L, "1-m"), generateScrappingLocation(2L, "1-d")),
//                        List.of(generateVisitedLocation(3L, 0), generateVisitedLocation(4L, 2)), 2)
//        );
//
//    }
//
//    private static ScrappingLocation generateScrappingLocation(long id, String freq) {
//        return ScrappingLocation.builder()
//                .id(id)
//                .url("mock")
//                .frequency(freq)
//                .nickname("mock")
//                .maxArticleScraped(1)
//                .build();
//    }
//
//    private static VisitedLocation generateVisitedLocation(long id, int days) {
//        return new VisitedLocation()
//                .id(id)
//                .visited(true)
//                .accessedAt(LocalDate.now().minusDays(days));
//    }

}