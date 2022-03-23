package com.engwili.arfe.dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class ReportSummary {
    private long scrappedArticleToday;
    private long totalArticlesAllTime;
    private long totalScrappingPlaces;
    private long scrappedPlacesToday;
}