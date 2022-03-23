package com.engwili.arfe.mapper;

import com.engwili.arfe.dto.response.ArticleDto;
import com.engwili.arfe.entity.Article;
import org.mapstruct.InjectionStrategy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;

@Mapper(componentModel = "spring", injectionStrategy = InjectionStrategy.CONSTRUCTOR, unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface ArticleMapper {

    @Mapping(source = "downloadableContentUrl", target = "url")
    @Mapping(source = "categories", target = "category")
    ArticleDto modelToDto(Article article);
}
