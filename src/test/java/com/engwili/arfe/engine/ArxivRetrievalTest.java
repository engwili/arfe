package com.engwili.arfe.engine;

import com.engwili.arfe.entity.Article;
import com.engwili.arfe.entity.ScrappingLocation;
import org.junit.jupiter.api.Test;

import java.util.List;

import static com.engwili.arfe.engine.ArxivRetrievalUtilTest.scrapingLocationOnArxiv;
import static org.assertj.core.api.Assertions.assertThat;

class ArxivRetrievalTest {

    @Test
    void given_validScrappingLocation_when() {
        ArxivRetrieval arxivRetrieval = new ArxivRetrieval();

        ScrappingLocation realScrappingLocation = ScrappingLocation.builder()
                .url(scrapingLocationOnArxiv)
                .maxArticleScraped(2)
                .nickname("arxiv")
                .build();

        List<Article> articles = arxivRetrieval.retrieveArticle(List.of(realScrappingLocation));

        assertThat(articles.size()).isEqualTo(2);
        assertThat(articles.get(0).getId()).isNull();
        assertThat(articles.get(1).getId()).isNull();
    }

}