package com.engwili.arfe.client.web;

import com.engwili.arfe.dto.request.WorkProofDto;
import com.engwili.arfe.dto.response.WorkStatusDto;
import com.engwili.arfe.entity.Status;
import com.engwili.arfe.entity.WorkStatus;
import com.engwili.arfe.exception.ArfeException;
import com.engwili.arfe.service.Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.util.UUID;
import java.util.concurrent.ExecutorService;

@Log4j2
@RestController
@RequiredArgsConstructor
public class TriggerController {

    private final Manager manager;
    private final ExecutorService executorService;

    @PostMapping(value = "/scrap/trigger", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkProofDto> triggerScrapping() {

        var workId = UUID.randomUUID().toString();

        return manager.retrieveWorkProofFor(Instant.now())
                .map(ResponseEntity::ok)
                .orElseGet(() -> {
                    executorService.submit(() -> {
                        manager.scrapArticlesAndStoreThem()
                                .whenComplete((el, throwable) -> manager.storeWorkStatus(new WorkStatus(Instant.now(), Status.FINISHED, workId)));
                    });
                    return ResponseEntity
                            .ok(manager.storeWorkStatus(new WorkStatus(Instant.now(), Status.IN_PROGRESS, workId)));
                });
    }

    @GetMapping(value = "/scrap/status", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<WorkStatusDto> triggerStatus(@RequestBody WorkProofDto workProofDto) {

        return manager.retrieveWorkProofFor(workProofDto.getWorkId())
                .map(ResponseEntity::ok)
                .orElseThrow(() -> new ArfeException(HttpStatus.NOT_FOUND, "Invalid work proof id"));
    }
}