package com.engwili.arfe.engine;

import com.engwili.arfe.entity.Article;
import org.assertj.core.api.Assertions;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.data.util.Pair;

import java.io.IOException;
import java.util.Optional;

class ArxivRetrievalUtilTest {

    private static Document document;
    static String articleLocationOnArxiv = "https://arxiv.org/abs/2202.00531";
    static String scrapingLocationOnArxiv = "https://arxiv.org/list/cs.AI/recent";

    @BeforeAll
    static void setup() {
        try {
            document = Jsoup.connect(articleLocationOnArxiv).get();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Test
    void when_retrieveArticleTitleFromWebpage_then_TitleIsRetrieved() {
        String titleFrom = ArxivRetrievalUtil.getTitleFrom(document);

        Assertions.assertThat(titleFrom).isNotEmpty();
    }

    @Test
    void when_retrieveArticleSummaryFromWebpage_then_SummaryIsRetrieved() {
        String titleFrom = ArxivRetrievalUtil.getSummaryFrom(document);

        Assertions.assertThat(titleFrom).isEqualTo("We consider the problem of multi-task reasoning (MTR), where an agent can solve multiple tasks via (first-order) logic reasoning. This capability is essential for human-like intelligence due to its strong generalizability and simplicity for handling multiple tasks. However, a major challenge in developing effective MTR is the intrinsic conflict between reasoning capability and efficiency. An MTR-capable agent must master a large set of \"skills\" to tackle diverse tasks, but executing a particular task at the inference stage requires only a small subset of immediately relevant skills. How can we maintain broad reasoning capability and also efficient specific-task performance? To address this problem, we propose a Planner-Reasoner framework capable of state-of-the-art MTR capability and high efficiency. The Reasoner models shareable (first-order) logic deduction rules, from which the Planner selects a subset to compose into efficient reasoning paths. The entire model is trained in an end-to-end manner using deep reinforcement learning, and experimental studies over a variety of domains validate its effectiveness.");
    }

    @Test
    void when_retrieveArticleAuthorsFromWebpage_then_AuthorsAreRetrieved() {
        String titleFrom = ArxivRetrievalUtil.getAuthorsFrom(document);

        Assertions.assertThat(titleFrom).isEqualTo("Daoming Lyu, Bo Liu, Jianshu Chen");
    }

    @Test
    void when_retrieveArticleCategoriesFromWebpage_then_CategoryIsRetrieved() {
        String titleFrom = ArxivRetrievalUtil.getCategoriesFrom(document);

        Assertions.assertThat(titleFrom).isEqualTo("Artificial Intelligence (cs.AI)");
    }

    @Test
    void when_retrieveArticleCreationDateFromWebpage_then_DateIsRetrieved() {
        String titleFrom = ArxivRetrievalUtil.getSubmissionDateFrom(document);

        Assertions.assertThat(titleFrom).isEqualTo("1 Feb 2022 16:22:19 UTC");
    }

    @Test
    void when_retrieveArticleSizeFromWebpage_then_SizeIsRetrieved() {
        String titleFrom = ArxivRetrievalUtil.getFullArticleSizeFrom(document);

        Assertions.assertThat(titleFrom).isNotEmpty();
    }

    @Test
    void when_retrieveArticleDownloadableContentURL_then_URLIsRetrieved() {
        String titleFrom = ArxivRetrievalUtil.getDownloadableContentURLFrom(document, articleLocationOnArxiv);
        Assertions.assertThat(titleFrom).isEqualTo("https://arxiv.org/pdf/2202.00531");
    }

    @Test
    void given_validURL_when_retrieveAllScrapedArticlesLinks_then_pathsAreRetrieved() {
        var scrappedLocations = ArxivRetrievalUtil.getLinksForAllArticlesScrapedFrom(scrapingLocationOnArxiv);
        Assertions.assertThat(scrappedLocations).isNotEmpty();
        Assertions.assertThat(scrappedLocations.get(0)).contains("abs");
    }

    @Test
    void given_invalidURL_when_retrieveAllScrapedArticlesLinks_then_pathsAreNotRetrieved() {
        var scrappedLocations = ArxivRetrievalUtil.getLinksForAllArticlesScrapedFrom("invalid url");
        Assertions.assertThat(scrappedLocations.size()).isZero();
    }

    @Test
    void when_stripAuthors_then_replaceWithThirdPluralPronoun() {
        String discovered = ArxivRetrievalUtil.stripAuthors("we discovered, and I discovered, in our paper");
        Assertions.assertThat(discovered).isEqualTo("the authors discovered, and the author discovered, in authors paper");
    }

    @Test
    void given_validURL_when_retrievingWebpageAsDocument_then_notNullDocumentIsGenerated() {
        Optional<Document> document = ArxivRetrievalUtil.retrieveWebpageAsDocument(articleLocationOnArxiv);
        Assertions.assertThat(document).isNotEmpty();
    }

    @Test
    void given_invalidURL_when_retrievingWebpageAsDocument_then_documentIsNotGenerated() {
        Optional<Document> document = ArxivRetrievalUtil.retrieveWebpageAsDocument("invalid url");
        Assertions.assertThat(document).isEmpty();
    }


    @Test
    void when_creatingArticleContainer_then_articleIsGenerated() {
        Optional<Document> document = ArxivRetrievalUtil.retrieveWebpageAsDocument(articleLocationOnArxiv);
        Assertions.assertThat(document).isNotEmpty();

        Optional<Article> articleContainer = ArxivRetrievalUtil
                .createArticleContainer(Pair.of(articleLocationOnArxiv, document), "arxiv");

        Assertions.assertThat(articleContainer).isNotEmpty();
        Assertions.assertThat(articleContainer.get().getTitle()).isNotEmpty();
        Assertions.assertThat(articleContainer.get().getCategories()).isEqualTo("Artificial Intelligence (cs.AI)");
        Assertions.assertThat(articleContainer.get().getDownloadableContentUrl()).isEqualTo("https://arxiv.org/pdf/2202.00531");
    }

    @Test
    void given_validUrl_when_getBaseUrl_then_retrieveBaseUrl() {
        Assertions.assertThat(ArxivRetrievalUtil.getBaseUrl(articleLocationOnArxiv)).isEqualTo("https://arxiv.org%s");
    }

    @Test
    void given_invalidUrl_when_getBaseUrl_then_returnInput() {
        Assertions.assertThat(ArxivRetrievalUtil.getBaseUrl("invalid url")).isEqualTo("invalid url");
    }
}