package com.engwili.arfe.misc.factory;

import com.engwili.arfe.entity.ScrappingLocation;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ObjectFactory {

    public static ScrappingLocation getLocation() {

        var urls = List.of("https://arxiv.org/list/cs.DC/recent",
                "https://arxiv.org/list/cs.AI/recent",
                "https://arxiv.org/list/cs.CC/recent",
                "https://arxiv.org/list/cs.CR/recent",
                "https://arxiv.org/list/cs.GT/recent");

        var scrappingLocations = urls
                .stream()
                .map(el -> ScrappingLocation.builder().maxArticleScraped(100).url(el).build())
                .collect(Collectors.toList());

        Collections.shuffle(scrappingLocations);

        return scrappingLocations.get(0);
    }

    public static ScrappingLocation getStandardLocation(){
        return ScrappingLocation.builder()
                .maxArticleScraped(100)
                .url("https://arxiv.org/list/cs.CE/recent")
                .frequency("1-d")
                .build();
    }
}
