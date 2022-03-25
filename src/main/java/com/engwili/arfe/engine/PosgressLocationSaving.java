package com.engwili.arfe.engine;

import com.engwili.arfe.entity.ScrappingLocation;
import com.engwili.arfe.entity.VisitedLocation;
import com.engwili.arfe.repository.VisitedLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Component
@RequiredArgsConstructor
public class PosgressLocationSaving implements LocationSaving {

    private final VisitedLocationRepository visitedLocationRepository;

    @Override
    public void saveVisitedLocation(List<ScrappingLocation> scrappingLocations) {

        var alreadyVisited = visitedLocationRepository
                .findByVisitedAndAccessedAt(true, LocalDate.now())
                .stream()
                .map(VisitedLocation::scrappingLocation)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());

        var toBeSaved = scrappingLocations
                .stream()
                .filter(el -> !alreadyVisited.contains(el))
                .collect(Collectors.toList());

        var visitedLocationsToBeAdded = toBeSaved
                .stream()
                .map(VisitedLocation::new)
                .collect(Collectors.toList());

        visitedLocationRepository.saveAll(visitedLocationsToBeAdded);
    }
}
