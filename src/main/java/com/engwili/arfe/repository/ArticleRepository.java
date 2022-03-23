package com.engwili.arfe.repository;

import com.engwili.arfe.entity.Article;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.PagingAndSortingRepository;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface ArticleRepository extends PagingAndSortingRepository<Article, Long> {
    List<Article> findByAccessedAt(LocalDate localDate);

    @Query(value = "SELECT COUNT(a.downloadable_content_url) FROM Article a ", nativeQuery = true)
    Integer countAllArticles();

    @Query(value = "SELECT COUNT(a.downloadable_content_url) FROM Article a where a.accessed_at > :date", nativeQuery = true)
    Integer countAllArticlesStartingFrom(@Param("date") LocalDate localDate);
}