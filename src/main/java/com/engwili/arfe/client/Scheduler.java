package com.engwili.arfe.client;

import com.engwili.arfe.client.web.TriggerController;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Objects;

@RequiredArgsConstructor
@Log4j2
@EnableAsync
@Service
public class Scheduler {

    private final TriggerController triggerController;

    @Scheduled(cron = " 0 0 13 * * ? ") // every day at 13:00 PM
    @Async
    public void runScrapPeriodically() {
        var workProofDtoResponseEntity = triggerController.triggerScrapping();
        log.info(String.format("Process triggered at %s with workId %s", LocalDateTime.now(),
                Objects.requireNonNull(workProofDtoResponseEntity.getBody()).getWorkId()));
    }
}
