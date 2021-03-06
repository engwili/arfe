package com.engwili.arfe.service;

import com.engwili.arfe.dto.request.WorkProofDto;
import com.engwili.arfe.dto.response.WorkStatusDto;
import com.engwili.arfe.engine.ArticleRetrieval;
import com.engwili.arfe.engine.LocationRetrieval;
import com.engwili.arfe.engine.LocationSaving;
import com.engwili.arfe.entity.WorkStatus;
import com.engwili.arfe.mapper.WorkMapper;
import com.engwili.arfe.repository.ArticleRepository;
import com.engwili.arfe.repository.WorkStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;


@RequiredArgsConstructor
@Service
public class ArxivPostgresManager implements Manager {

    private final ArticleRetrieval articleRetrieval;
    private final LocationRetrieval locationRetrieval;
    private final ArticleRepository articleRepository;
    private final LocationSaving locationSaving;
    private final ExecutorService executorService;
    private final WorkStatusRepository workStatusRepository;
    private final WorkMapper workMapper;

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public CompletableFuture<Integer> scrapArticlesAndStoreThem() {
        var unvisitedScrappingLocations = locationRetrieval.retrieveUnvisitedScrappingLocations();

        var scrappedArticleCount = CompletableFuture
                .supplyAsync(() -> unvisitedScrappingLocations, executorService)
                .thenApplyAsync(articleRetrieval::retrieveArticle, executorService)
                .whenComplete((articles, s) -> articleRepository.saveAll(articles))
                .thenApplyAsync(List::size, executorService);

        var savedVisitedLocation = CompletableFuture
                .supplyAsync(() -> unvisitedScrappingLocations, executorService)
                .thenAcceptAsync(locationSaving::saveVisitedLocation, executorService)
                .toCompletableFuture();

        CompletableFuture.allOf(scrappedArticleCount, savedVisitedLocation);

        return scrappedArticleCount;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public WorkProofDto storeWorkStatus(WorkStatus workStatus) {
        var saved = workStatusRepository.save(workStatus);
        return workMapper.toWorkProof(saved);
    }

    @Override
    public Optional<WorkProofDto> retrieveWorkProofFor(Instant instant) {

        return locationRetrieval.retrieveUnvisitedScrappingLocations().size() == 0 ? workStatusRepository
                .findByTriggeredAtBefore(instant)
                .stream()
                .max(Comparator.comparing(WorkStatus::getTriggeredAt))
                .map(workMapper::toWorkProof) : Optional.empty();
    }

    @Override
    public Optional<WorkStatusDto> retrieveWorkProofFor(String workId) {
        return workStatusRepository
                .findByWorkId(workId)
                .stream()
                .max(Comparator.comparing(WorkStatus::getTriggeredAt))
                .map(workMapper::toWorkStatus);
    }
}