package com.engwili.arfe.engine;

import com.engwili.arfe.dto.value.ArticlePage;
import com.engwili.arfe.entity.Article;
import com.engwili.arfe.entity.ScrappingLocation;
import lombok.extern.log4j.Log4j2;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;
import org.springframework.data.util.Pair;

import java.time.LocalDate;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.Random;
import java.util.stream.Collectors;

@Log4j2
public class ArxivRetrievalUtil {

    public static String stripAuthors(String text) {
        return text
                .replaceAll("\\b(We)+\\b", "The authors")
                .replaceAll("\\b(we)+\\b", "the authors")
                .replaceAll("\\b(our)+\\b", "authors")
                .replaceAll("\\b(In this paper)+\\b", "The authors")
                .replaceAll("\\b(in this paper)+\\b", "the authors")
                .replaceAll("\\b(I)+\\b", "the author")
                .replaceAll("\\b(Our)+\\b", "Authors");
    }

    public static String getBaseUrl(String url) {
        List<String> list = List.of(url.split("/"));
        return list.size() > 1 ? String.join("",
                String.join("//", list.get(0), list.get(1)),
                String.join("", list.get(2), "%s")) : url;
    }

    public static List<String> getLinksForAllArticlesScrapedFrom(String webpage) {
        String composed = ArxivRetrievalUtil.getBaseUrl(webpage);
        Document document = null;
        try {
            document = Jsoup.connect(webpage).get();
            Elements content = document.getElementsByClass("list-identifier");

            return content.stream()
                    .flatMap(el -> el.getElementsByTag("a").stream())
                    .filter(el -> el.attr("title").toLowerCase(Locale.ROOT).equalsIgnoreCase("Abstract"))
                    .map(el -> el.attr("href"))
                    .map(el -> String.format(composed, el))
                    .collect(Collectors.toList());

        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return List.of();
    }

    public static Optional<Document> retrieveWebpageAsDocument(String webpage) {

        try {
            Document document = null;
            document = Jsoup.connect(webpage).get();
            return Optional.of(document);

        } catch (Exception e) {
            log.info(e.getMessage());
        }

        return Optional.empty();
    }

    public static Optional<Article> createArticleContainer(Pair<String, Optional<Document>> pair, String nickName) {

        if (pair.getSecond().isEmpty())
            return Optional.empty();

        var document = pair.getSecond().get();
        var webpage = pair.getFirst();

        Article article = Article.builder()
                .authors(getAuthorsFrom(document))
                .createdAt(getSubmissionDateFrom(document))
                .categories(getCategoriesFrom(document))
                .summary(stripAuthors(getSummaryFrom(document)))
                .title(getTitleFrom(document))
                .accessedAt(LocalDate.now())
                .downloadableContentUrl(getDownloadableContentURLFrom(document, webpage))
                .build();


        return Optional.of(article);
    }

    public static String getTitleFrom(Document document) {
        Elements content = document.getElementsByClass("title mathjax");
        List<String> split = List.of(content.get(0).text().split("Title:"));
        return (split.get(1).trim());
    }

    public static String getSummaryFrom(Document document) {
        Elements content = document.getElementsByClass("abstract mathjax");
        List<String> split = List.of(content.get(0).text().split("Abstract:"));
        return (split.get(1).trim());
    }

    public static String getAuthorsFrom(Document document) {
        Elements content = document.getElementsByClass("authors");
        List<String> split = List.of(content.get(0).text().split("Authors:"));
        return (split.get(1).trim());

    }

    public static String getCategoriesFrom(Document document) {
        Elements content = document.getElementsByClass("primary-subject");
        return (content.get(0).text().trim());
    }

    public static String getSubmissionDateFrom(Document document) {
        Elements content = document.getElementsByClass("submission-history");
        var firstSplit = content.text().split("UTC");
        var finalSplit = firstSplit[0].split(",");
        return (finalSplit[1].trim() + " " + "UTC");
    }

    public static String getFullArticleSizeFrom(Document document) {
        Elements content = document.getElementsByClass("submission-history");
        var firstSplit = content.text().split("UTC");
        var result = firstSplit[1].substring(2, firstSplit[1].length() - 1);
        return (result.trim());
    }

    public static String getDownloadableContentURLFrom(Document document, String url) {
        Elements content = document.getElementsByClass("full-text");
        var suffix = content.select("li").get(0).select("a").attr("href");
        return (String.format(getBaseUrl(url), suffix));
    }

    public static List<Article> retrieveAtMostNArticlesFromLocation(ScrappingLocation location) {

        int maxArticles = location.maxArticleScraped();

        return getLinksForAllArticlesScrapedFrom(location.url())
                .stream()
                .map(ArxivRetrievalUtil::retrieveWebpageAsDocument)
                .map(el -> ArxivRetrievalUtil.createArticleContainer(Pair.of(location.url(), el), location.nickname()))
                .limit(maxArticles)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .collect(Collectors.toList());
    }

    public static ArticlePage computeArticlePage(int totalArticles) {
        var random = new Random();
        var articleId = random.nextInt(totalArticles - 1);
        var size = 5;
        var page = articleId / size;
        var articlePosition = articleId % size;
        return new ArticlePage(page, size, articlePosition);
    }
}
