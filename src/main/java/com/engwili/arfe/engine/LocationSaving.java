package com.engwili.arfe.engine;

import com.engwili.arfe.entity.ScrappingLocation;

import java.util.List;

public interface LocationSaving {

    void saveVisitedLocation(List<ScrappingLocation> scrappingLocations);
}
