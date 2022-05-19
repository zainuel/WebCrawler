package com.crawler;

import com.crawler.crawl.strategy.SameDomainCrawlStrategy;
import com.crawler.crawler.Crawler;
import com.crawler.fetcher.Fetcher;
import com.crawler.model.Graph;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.jsoup.select.Elements;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.LinkedBlockingQueue;

import static org.mockito.ArgumentMatchers.any;

class CrawlerTest {

    Document document = Mockito.mock(Document.class);
    private BlockingQueue<URL> urlsToVisit;
    private Set<URL> visitedUrls;
    private Graph crawlerGraph;
    private Crawler crawler;
    private URL rootUrl;
    private Elements elements;

    @BeforeEach
    void setUp() throws MalformedURLException {
        urlsToVisit = new LinkedBlockingQueue<>();
        visitedUrls = ConcurrentHashMap.newKeySet();
        crawlerGraph = new Graph();
        rootUrl = new URL("http://www.website.com");
        crawler = buildCrawler();
        elements = new Elements();
    }

    @Test
    public void shouldIgnoreUnreachableUrls() throws MalformedURLException {
        try (MockedStatic<Fetcher> mockedFetcher = Mockito.mockStatic(Fetcher.class)) {
            mockedFetcher.when(() -> Fetcher.fetch(any())).thenThrow(new IOException());

            URL unreachableUrl = new URL("http://unreachableUrl.com");
            urlsToVisit.add(unreachableUrl);

            crawler.run();

            Assertions.assertEquals(0, urlsToVisit.size());
            Assertions.assertEquals(0, crawlerGraph.getNodeToEdgesMap().size());
            Assertions.assertEquals(1, visitedUrls.size());
            Assertions.assertTrue(visitedUrls.contains(unreachableUrl));

        }
    }

    @Test
    void shouldCrawlRootUrlAndVisitSameHostLinks() throws MalformedURLException {
        try (MockedStatic<Fetcher> mockedFetcher = Mockito.mockStatic(Fetcher.class)) {
            mockedFetcher.when(() -> Fetcher.fetch(any())).thenReturn(document);
            Mockito.when(document.select("a[href]")).thenReturn(elements).thenReturn(new Elements());

            String diffHostUrl = "http://differentHost.com/";
            elements.add(new Element(Tag.valueOf("a"), rootUrl.toString()).attr("href", "/person/"));
            elements.add(new Element(Tag.valueOf("a"), diffHostUrl).attr("href", "/support"));

            urlsToVisit.add(rootUrl);
            URL linkedUrl = new URL(rootUrl.toString() + "/person/");

            crawler.run();


            Assertions.assertEquals(0, urlsToVisit.size());
            Assertions.assertEquals(2, visitedUrls.size());
            Assertions.assertTrue(visitedUrls.contains(rootUrl));
            Assertions.assertTrue(visitedUrls.contains(linkedUrl));
            Assertions.assertTrue(crawlerGraph.contains(rootUrl));
            Assertions.assertTrue(crawlerGraph.contains(linkedUrl));
        }
    }

    @Test
    void shouldNotCrawlUrlFromDifferentHost() throws MalformedURLException {
        try (MockedStatic<Fetcher> mockedFetcher = Mockito.mockStatic(Fetcher.class)) {
            mockedFetcher.when(() -> Fetcher.fetch(any())).thenReturn(document);
            URL differentHostUrl = new URL("http://www.differentHost.com/");
            elements.add(new Element(Tag.valueOf("a"), differentHostUrl.toString()).attr("href", "/support/"));
            Mockito.when(document.select("a[href]")).thenReturn(elements);
            urlsToVisit.add(rootUrl);

            crawler.run();

            Assertions.assertEquals(0, urlsToVisit.size());
            Assertions.assertEquals(1, visitedUrls.size());
            Assertions.assertFalse(visitedUrls.contains(differentHostUrl));
            Assertions.assertFalse(crawlerGraph.contains(differentHostUrl));
            Assertions.assertTrue(crawlerGraph.contains(rootUrl));
            Assertions.assertTrue(crawlerGraph.getNodeToEdgesMap().get(rootUrl).isEmpty());
        }
    }

    @Test
    void shouldIgnoreInvalidUrls() throws MalformedURLException {
        try (MockedStatic<Fetcher> mockedFetcher = Mockito.mockStatic(Fetcher.class)) {
            mockedFetcher.when(() -> Fetcher.fetch(rootUrl.toString())).thenReturn(document);
            URL invalidUrl = new URL("http:/ww.differm/");
            elements.add(new Element(Tag.valueOf("a"), invalidUrl.toString()).attr("href", "javascript:void(0)"));
            Mockito.when(document.select("a[href]")).thenReturn(elements);
            urlsToVisit.add(rootUrl);

            crawler.run();

            Assertions.assertEquals(0, urlsToVisit.size());
            Assertions.assertEquals(1, visitedUrls.size());
            Assertions.assertFalse(visitedUrls.contains(invalidUrl));
            Assertions.assertFalse(crawlerGraph.contains(invalidUrl));
            Assertions.assertTrue(crawlerGraph.getNodeToEdgesMap().get(rootUrl).isEmpty());
        }
    }


    @Test
    void shouldNotCrawlVisitedLinks() throws MalformedURLException {
        try (MockedStatic<Fetcher> mockedFetcher = Mockito.mockStatic(Fetcher.class)) {
            mockedFetcher.when(() -> Fetcher.fetch(any())).thenReturn(document);
            elements.add(new Element(Tag.valueOf("a"), rootUrl.toString()).attr("href", "/person/"));
            elements.add(new Element(Tag.valueOf("a"), rootUrl.toString()).attr("href", ""));
            Mockito.when(document.select("a[href]")).thenReturn(elements);

            URL linkedUrl = new URL(rootUrl + "/person/");
            urlsToVisit.add(rootUrl);

            crawler.run();

            Assertions.assertEquals(0, urlsToVisit.size());
            Assertions.assertEquals(2, visitedUrls.size());
            Assertions.assertTrue(visitedUrls.contains(rootUrl));
            Assertions.assertTrue(visitedUrls.contains(linkedUrl));
            Assertions.assertTrue(crawlerGraph.contains(rootUrl));
            Assertions.assertEquals(1, crawlerGraph.get(rootUrl).size());
            Assertions.assertTrue(crawlerGraph.contains(linkedUrl));
            Assertions.assertEquals(0, crawlerGraph.get(linkedUrl).size());
        }
    }

    private Crawler buildCrawler() {
        return Crawler.builder()
                .urlsToVisitQueue(urlsToVisit)
                .visitedUrls(visitedUrls)
                .crawlerGraph(crawlerGraph)
                .rootUrl(rootUrl)
                .crawlStrategy(new SameDomainCrawlStrategy())
                .build();
    }
}