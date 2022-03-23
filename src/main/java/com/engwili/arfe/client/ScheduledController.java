package com.engwili.arfe.client;

import com.engwili.arfe.service.Manager;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.scheduling.annotation.Scheduled;

import java.time.Instant;
import java.util.concurrent.CompletableFuture;

import static java.lang.String.format;

@Log4j2
@RequiredArgsConstructor
class ScheduledController {

    private final Manager manager;
    //todo make it work

//    @Scheduled
    void scheduledRun() {
        log.info(format("Starting the scheduled run for %s at time %s",
                manager.getClass().getSimpleName().toUpperCase(), Instant.now().toString()));

        CompletableFuture<Integer> integerCompletableFuture = manager.scrapArticlesAndStoreThem();

        //todo add aop and time the fastness of retrieving how many files

        log.info(format("Ending the scheduled run for %s at time %s",
                manager.getClass().getSimpleName().toUpperCase(), Instant.now().toString()));
    }

}
