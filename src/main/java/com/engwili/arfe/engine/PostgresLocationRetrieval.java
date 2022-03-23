package com.engwili.arfe.engine;

import com.engwili.arfe.entity.ScrappingLocation;
import com.engwili.arfe.entity.VisitedLocation;
import com.engwili.arfe.repository.ScrappingLocationRepository;
import com.engwili.arfe.repository.VisitedLocationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import static com.engwili.arfe.engine.PostgresLocationRetrievalUtil.isVisitedLocationReadyForANewVisit;

@Component
@RequiredArgsConstructor
public class PostgresLocationRetrieval implements LocationRetrieval {

    private final ScrappingLocationRepository scrappingLocationRepository;
    private final VisitedLocationRepository visitedLocationRepository;

    @Override
    public List<ScrappingLocation> retrieveUnvisitedScrappingLocations() {

        var allScrappingLocations = scrappingLocationRepository.findAll();

        var scrappingLocationsFromAlreadyVisitedPlaces = visitedLocationRepository
                .findByVisitedAndAccessedAt(true, LocalDate.now())
                .stream()
                .filter(el -> !isVisitedLocationReadyForANewVisit(el))
                .map(VisitedLocation::scrappingLocation)
                .collect(Collectors.toList());

        allScrappingLocations.removeAll(scrappingLocationsFromAlreadyVisitedPlaces);

        return allScrappingLocations;
    }

}
