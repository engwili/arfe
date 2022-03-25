package com.engwili.arfe.service;

import com.engwili.arfe.engine.ArticleRetrieval;
import com.engwili.arfe.engine.LocationRetrieval;
import com.engwili.arfe.engine.PosgressLocationSaving;
import com.engwili.arfe.entity.*;
import com.engwili.arfe.mapper.WorkMapper;
import com.engwili.arfe.repository.ArticleRepository;
import com.engwili.arfe.repository.ScrappingLocationRepository;
import com.engwili.arfe.repository.VisitedLocationRepository;
import com.engwili.arfe.repository.WorkStatusRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@SpringBootTest
@AutoConfigureTestDatabase
@ActiveProfiles("test")
@Transactional
@DirtiesContext(methodMode = DirtiesContext.MethodMode.BEFORE_METHOD)
class ArxivPostgresManagerTest {

    @Autowired
    private ArticleRetrieval articleRetrieval;

    @Autowired
    private LocationRetrieval locationRetrieval;

    @MockBean
    private ArticleRepository articleRepository;

    @MockBean
    private WorkStatusRepository workStatusRepository;

    @Autowired
    private ScrappingLocationRepository scrappingLocationRepository;

    @Autowired
    private VisitedLocationRepository visitedLocationRepository;

    @Autowired
    private PosgressLocationSaving posgressLocationSaving;

    @Autowired
    private WorkMapper workMapper;
//
//    @ClassRule
//    public static PostgreSQLContainer postgreSQLContainer = new PostgreSQLContainer("postgres:11.1")
//            .withDatabaseName("integration-tests-db")
//            .withUsername("sa")
//            .withPassword("sa");
//
//    static class Initializer
//            implements ApplicationContextInitializer<ConfigurableApplicationContext> {
//        public void initialize(ConfigurableApplicationContext configurableApplicationContext) {
//            TestPropertyValues.of(
//                    "spring.datasource.url=" + postgreSQLContainer.getJdbcUrl(),
//                    "spring.datasource.username=" + postgreSQLContainer.getUsername(),
//                    "spring.datasource.password=" + postgreSQLContainer.getPassword()
//            ).applyTo(configurableApplicationContext.getEnvironment());
//        }
//    }

    private static final String arxivAI = "https://arxiv.org/list/cs.AI/recent";
    private static final String arxivDS = "https://arxiv.org/list/cs.DS/recent";

    @DisplayName("only unvisited locations are scrapped give multiple scrapping location")
    @Test
    void given_2ScrappingLocationAnd1AlreadyVisitedLocation_when_scrapArticlesAndStoreThem_then_onlyUnvisitedLocationIsScrapped()
            throws ExecutionException, InterruptedException, TimeoutException {

        ArgumentCaptor<ArrayList<Article>> captor = ArgumentCaptor.forClass(ArrayList.class);

        ArxivPostgresManager arxivPostgresManager = new ArxivPostgresManager(articleRetrieval, locationRetrieval,
                articleRepository, posgressLocationSaving,
                Executors.newCachedThreadPool(), workStatusRepository, workMapper);

        var scrappingLocation1 = ScrappingLocation.builder()
                .maxArticleScraped(2)
                .url(arxivAI)
                .frequency("1-d")
                .nickname("arxiv-AI")
                .build();

        var scrappingLocation2 = ScrappingLocation.builder()
                .maxArticleScraped(2)
                .url(arxivDS)
                .frequency("1-d")
                .nickname("arxiv-DS")
                .build();

        scrappingLocation1 = scrappingLocationRepository.save(scrappingLocation1);
        scrappingLocation2 = scrappingLocationRepository.save(scrappingLocation2);

        var visitedLocation = new VisitedLocation();

        visitedLocation = visitedLocation
                .scrappingLocation(scrappingLocation2)
                .visited(true)
                .accessedAt(LocalDate.now());

        visitedLocationRepository.save(visitedLocation);

        Assertions.assertThat(scrappingLocationRepository.findAll().size()).isEqualTo(2);
        Assertions.assertThat(visitedLocationRepository.findAll().size()).isOne();

        CompletableFuture<Integer> result = arxivPostgresManager.scrapArticlesAndStoreThem();

        var count = result.get(30, TimeUnit.SECONDS);

        assertThat(count).isEqualTo(2);

        Mockito.verify(articleRepository).saveAll(captor.capture());

        var articlesSaved = captor.getAllValues().get(0);

        var visited = visitedLocationRepository.findByVisitedAndAccessedAt(true, LocalDate.now());

        assertThat(articlesSaved).hasSize(2);
        assertThat(articlesSaved.get(0).getSummary()).isNotEmpty();
        assertThat(articlesSaved.get(0).getTitle()).isNotEmpty();
        assertThat(articlesSaved.get(0).getDownloadableContentUrl()).isNotEmpty();
        assertThat(articlesSaved.get(0).getCategories()).isEqualTo("Artificial Intelligence (cs.AI)");
        assertThat(articlesSaved.get(1).getSummary()).isNotEmpty();
        assertThat(articlesSaved.get(1).getTitle()).isNotEmpty();
        assertThat(articlesSaved.get(1).getDownloadableContentUrl()).isNotEmpty();
        assertThat(articlesSaved.get(1).getCategories()).isEqualTo("Artificial Intelligence (cs.AI)");

        assertThat(visited).hasSize(2);
    }

    @DisplayName("retrieve the latest work proof from work id with success")
    @Test
    void retrieveWorkProofFromWorkId() {

        ArxivPostgresManager arxivPostgresManager = new ArxivPostgresManager(articleRetrieval, locationRetrieval,
                articleRepository, posgressLocationSaving,
                Executors.newCachedThreadPool(), workStatusRepository, workMapper);
        var workId = "uuid";
        var now = Instant.now();
        var workInProgress = new WorkStatus(now, Status.IN_PROGRESS, workId);
        var workFinished = new WorkStatus(now.plus(1L, ChronoUnit.SECONDS), Status.FINISHED, workId);

        when(workStatusRepository.findByWorkId(workId)).thenReturn(List.of(workInProgress, workFinished));

        var workStatusDto = arxivPostgresManager.retrieveWorkProofFor(workId);

        assertThat(workStatusDto).isPresent();
        assertThat(workStatusDto.get().getTriggeredAt()).isEqualTo(now.plus(1L, ChronoUnit.SECONDS).toString());
    }

    @DisplayName("retrieve the latest work proof from INSTANT with success given no unvisited locations")
    @Test
    void retrieveWorkProofFromInstantNoUnvisitedLocations() {
        ArxivPostgresManager arxivPostgresManager = new ArxivPostgresManager(articleRetrieval, locationRetrieval,
                articleRepository, posgressLocationSaving,
                Executors.newCachedThreadPool(), workStatusRepository, workMapper);
        var firstWorkId = "uuid";
        var now = Instant.now();
        var workInProgress = new WorkStatus(now.minus(1L, ChronoUnit.SECONDS), Status.IN_PROGRESS, firstWorkId);
        var workFinished = new WorkStatus(now, Status.FINISHED, firstWorkId);

        when(workStatusRepository.findByTriggeredAtBefore(any())).thenReturn(List.of(workInProgress, workFinished));

        var workStatusDto = arxivPostgresManager.retrieveWorkProofFor(Instant.now());

        assertThat(workStatusDto).isPresent();
        assertThat(workStatusDto.get().getWorkId()).isEqualTo(firstWorkId);
    }

    @DisplayName("retrieve the latest work proof from INSTANT returns empty value given one unvisited locations")
    @Test
    void retrieveWorkProofFromInstantOneUnvisitedLocation() {
        ArxivPostgresManager arxivPostgresManager = new ArxivPostgresManager(articleRetrieval, locationRetrieval,
                articleRepository, posgressLocationSaving,
                Executors.newCachedThreadPool(), workStatusRepository, workMapper);
        var firstWorkId = "uuid";
        var now = Instant.now();
        var workInProgress = new WorkStatus(now.minus(1L, ChronoUnit.SECONDS), Status.IN_PROGRESS, firstWorkId);
        var workFinished = new WorkStatus(now, Status.FINISHED, firstWorkId);

        when(workStatusRepository.findByTriggeredAtBefore(any())).thenReturn(List.of(workInProgress, workFinished));

        var scrappingLocation2 = ScrappingLocation.builder()
                .maxArticleScraped(2)
                .url(arxivDS)
                .nickname("arxiv-DS")
                .frequency("1-d")
                .build();

        scrappingLocation2 = scrappingLocationRepository.save(scrappingLocation2);

        var workStatusDto = arxivPostgresManager.retrieveWorkProofFor(Instant.now());

        assertThat(workStatusDto).isEmpty();
    }

}