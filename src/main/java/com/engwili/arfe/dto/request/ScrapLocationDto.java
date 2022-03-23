package com.engwili.arfe.dto.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.validation.constraints.Pattern;

@Setter
@NoArgsConstructor
@AllArgsConstructor
@Getter
public class ScrapLocationDto {
    private String url;

    @Pattern(regexp = "[1-9]-[dwm]", message = "frequency format invalid: the correct format is [1-9]-[dwm]")
    private String frequency;
    private String nickname;
    private Integer maxArticleScraped;
}