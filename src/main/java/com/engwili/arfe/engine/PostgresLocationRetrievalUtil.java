package com.engwili.arfe.engine;

import com.engwili.arfe.entity.VisitedLocation;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;

@Log4j2
public class PostgresLocationRetrievalUtil {

    public static boolean isVisitedLocationReadyForANewVisit(VisitedLocation visitedLocation) {
        var accessedAt = visitedLocation.accessedAt();
        var integerChronoUnitPair = visitedLocation.scrappingLocation().convertFrequencyToChronUnit();
        return LocalDateTime.of(accessedAt, LocalTime.of(0, 0))
                .plus(integerChronoUnitPair.getFirst(), integerChronoUnitPair.getSecond())
                .isBefore(LocalDateTime.of(LocalDate.now(), LocalTime.now()));
    }
}
