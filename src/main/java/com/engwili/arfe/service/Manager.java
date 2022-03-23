package com.engwili.arfe.service;

import com.engwili.arfe.dto.request.WorkProofDto;
import com.engwili.arfe.dto.response.WorkStatusDto;
import com.engwili.arfe.entity.WorkStatus;

import java.time.Instant;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;

public interface Manager {

    CompletableFuture<Integer> scrapArticlesAndStoreThem();

    WorkProofDto storeWorkStatus(WorkStatus workStatus);

    Optional<WorkProofDto> retrieveWorkProofFor(Instant instant);

    Optional<WorkStatusDto> retrieveWorkProofFor(String workId);
}