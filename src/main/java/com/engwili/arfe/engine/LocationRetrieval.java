package com.engwili.arfe.engine;

import com.engwili.arfe.entity.ScrappingLocation;

import java.util.List;

public interface LocationRetrieval {

    List<ScrappingLocation> retrieveUnvisitedScrappingLocations();
}
