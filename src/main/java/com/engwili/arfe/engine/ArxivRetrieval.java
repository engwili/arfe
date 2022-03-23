package com.engwili.arfe.engine;

import com.engwili.arfe.entity.Article;
import com.engwili.arfe.entity.ScrappingLocation;
import org.springframework.stereotype.Component;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

@Component
public class ArxivRetrieval implements ArticleRetrieval {

    @Override
    public List<Article> retrieveArticle(List<ScrappingLocation> locations) {

        return locations
                .stream()
                .parallel()
                .map(ArxivRetrievalUtil::retrieveAtMostNArticlesFromLocation)
                .flatMap(Collection::stream)
                .collect(Collectors.toList());
    }
}
