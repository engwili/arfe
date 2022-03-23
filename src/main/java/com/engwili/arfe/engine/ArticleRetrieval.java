package com.engwili.arfe.engine;

import com.engwili.arfe.entity.Article;
import com.engwili.arfe.entity.ScrappingLocation;

import java.util.List;

public interface ArticleRetrieval {

    List<Article> retrieveArticle(List<ScrappingLocation> locations);
}
