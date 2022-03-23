package com.engwili.arfe.dto.response;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@NoArgsConstructor
@Getter
@EqualsAndHashCode
public class ArticleDto {

    private String title;
    private String summary;
    private String authors;
    private String url;
    private String category;
}
