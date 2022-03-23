package com.engwili.arfe.client.web;

import com.engwili.arfe.dto.response.ArticleDto;
import com.engwili.arfe.mapper.ArticleMapper;
import com.engwili.arfe.repository.ArticleRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.data.domain.PageRequest;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.constraints.Min;
import java.util.List;
import java.util.stream.Collectors;

import static com.engwili.arfe.engine.ArxivRetrievalUtil.computeArticlePage;

@RestController
@RequiredArgsConstructor
@Validated
@Log4j2
public class ArticleController {

    private final ArticleRepository articleRepository;
    private final ArticleMapper articleMapper;

    @GetMapping("/articles")
    public List<ArticleDto> getArticle(@RequestParam("page") @Min(0) Integer page, @RequestParam("size") @Min(1) Integer size) {
        return articleRepository.findAll(PageRequest.of(page, size))
                .stream()
                .map(articleMapper::modelToDto)
                .collect(Collectors.toList());
    }

    @GetMapping("/article/random")
    public ArticleDto getRandomArticle() {

        var articlePage = computeArticlePage(articleRepository.countAllArticles());

        return articleRepository.findAll(PageRequest.of(articlePage.page(), articlePage.size()))
                .stream()
                .map(articleMapper::modelToDto)
                .collect(Collectors.toList())
                .get(articlePage.articleIndex());
    }
}
