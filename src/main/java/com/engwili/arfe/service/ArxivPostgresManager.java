package com.engwili.arfe.service;

import com.engwili.arfe.dto.request.WorkProofDto;
import com.engwili.arfe.dto.response.WorkStatusDto;
import com.engwili.arfe.engine.ArticleRetrieval;
import com.engwili.arfe.engine.LocationRetrieval;
import com.engwili.arfe.engine.LocationSaving;
import com.engwili.arfe.entity.Article;
import com.engwili.arfe.entity.WorkStatus;
import com.engwili.arfe.mapper.WorkMapper;
import com.engwili.arfe.repository.ArticleRepository;
import com.engwili.arfe.repository.WorkStatusRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.ArrayList;
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
    public CompletableFuture<Integer> scrapArticlesAndStoreThem() {
        var unvisitedScrappingLocations = locationRetrieval.retrieveUnvisitedScrappingLocations();

//
//
//        //todo aici e problema si la work status ca se genereaza de fiecare data si nu o singura data pe trigger
//        var result = CompletableFuture
//                .supplyAsync(() -> unvisitedScrappingLocations, executorService)
//                .thenApplyAsync(articleRetrieval::retrieveArticle, executorService);
//
////        var result2 = CompletableFuture
////                .supplyAsync(() -> result.thenCombine(), executorService)
////                .thenAcceptAsync(el ->)
////                .whenComplete((list, throwable) -> {
////                    articleRepository.saveAll(list);
////                    return list.size();
////                });
//
////        result
////                .thenApplyAsync(List::size, executorService)
////                .toCompletableFuture();
//
//        var saved = CompletableFuture
//                .supplyAsync(() -> unvisitedScrappingLocations)
////                .whenComplete((el, th) -> locationSaving.saveVisitedLocation(unvisitedScrappingLocations))
//                .thenAcceptAsync(locationSaving::saveVisitedLocation, executorService)
//                .toCompletableFuture();
////
////        CompletableFuture
////                .supplyAsync(() -> unvisitedScrappingLocations, executorService)
////                .thenAcceptAsync(locationSaving::saveVisitedLocation, executorService)
////                .toCompletableFuture();
//
//        return result;

        var result = CompletableFuture
                .supplyAsync(() -> unvisitedScrappingLocations, executorService)
                .thenApplyAsync(articleRetrieval::retrieveArticle, executorService)
                .whenComplete((articles,s) -> articleRepository.saveAll(articles))
                .thenApplyAsync(List::size, executorService);

        var savedVisitedLocation = CompletableFuture
                .supplyAsync(() -> unvisitedScrappingLocations, executorService)
                .thenAcceptAsync(locationSaving::saveVisitedLocation, executorService)
                .toCompletableFuture();

        CompletableFuture.allOf(result, savedVisitedLocation);

//        var savedArticles = result
//
//        CompletableFuture<Integer> integerCompletableFuture = result
//                .thenCompose(el -> countedRetrievedArticles);
//
//        CompletableFuture
//                .supplyAsync(() -> unvisitedScrappingLocations)
//                .thenAcceptAsync(locationSaving::saveVisitedLocation, executorService)
//                .toCompletableFuture();

//        return countedRetrievedArticles;
        return result;
    }

    @Override
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