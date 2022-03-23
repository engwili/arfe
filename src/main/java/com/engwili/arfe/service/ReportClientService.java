package com.engwili.arfe.service;

import com.engwili.arfe.dto.response.ReportSummary;
import com.engwili.arfe.repository.ArticleRepository;
import com.engwili.arfe.repository.ScrappingLocationRepository;
import com.engwili.arfe.repository.VisitedLocationRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;

@Service
@RequiredArgsConstructor
@Log4j2
public class ReportClientService {

    private final ScrappingLocationRepository scrappingLocationRepository;
    private final ArticleRepository articleRepository;
    private final VisitedLocationRepository visitedLocationRepository;
    private final ExecutorService executorService;

    public ReportSummary getReportSummary() throws ExecutionException, InterruptedException {
        CompletableFuture<Integer> totalScrappingPlaces = CompletableFuture
                .supplyAsync(() -> scrappingLocationRepository.findAll().size(), executorService);

        CompletableFuture<Integer> totalArticlesAllTime = CompletableFuture
                .supplyAsync(articleRepository::countAllArticles);

        CompletableFuture<Integer> scrappedArticleToday = CompletableFuture
                .supplyAsync(() -> articleRepository.countAllArticlesStartingFrom(LocalDate.now()));


        CompletableFuture<Integer> scrappingPlacesToday = CompletableFuture
                .supplyAsync(() -> visitedLocationRepository.findByVisitedAndAccessedAt(true, LocalDate.now()).size());

        CompletableFuture<Void> voidCompletableFuture = CompletableFuture.
                allOf(totalScrappingPlaces, totalArticlesAllTime, scrappedArticleToday, scrappingPlacesToday);

        voidCompletableFuture.get();

        return new ReportSummary(scrappedArticleToday.get(), totalArticlesAllTime.get(),
                totalScrappingPlaces.get(), scrappingPlacesToday.get());
    }
}
