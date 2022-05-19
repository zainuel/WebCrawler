package com.crawler.crawler;

import com.crawler.crawl.strategy.CrawlStrategy;
import com.crawler.exceptions.InvalidInputException;
import com.crawler.fetcher.Fetcher;
import com.crawler.model.Graph;
import com.crawler.utils.UrlUtil;
import lombok.Builder;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Slf4j
@Builder
public class Crawler implements Runnable {

    private static final int IDLE_TIMEOUT_IN_MILLIS = 3000;

    @NonNull
    private final URL rootUrl;
    @NonNull
    private final BlockingQueue<URL> urlsToVisitQueue;
    @NonNull
    private final Set<URL> visitedUrls;
    @NonNull
    private final CrawlStrategy crawlStrategy;
    @NonNull
    private final Graph crawlerGraph;

    public Crawler(URL rootUrl, BlockingQueue<URL> urlsToVisitQueue, Set<URL> visitedUrls, CrawlStrategy crawlStrategy, Graph crawlerGraph) {
        this.rootUrl = rootUrl;
        this.urlsToVisitQueue = urlsToVisitQueue;
        this.visitedUrls = visitedUrls;
        this.crawlStrategy = crawlStrategy;
        this.crawlerGraph = crawlerGraph;
    }

    /*
     * Run the crawler until the queue is empty
     */
    @Override
    public void run() {

        while (true) {
            try {
                URL url = urlsToVisitQueue.poll(IDLE_TIMEOUT_IN_MILLIS, TimeUnit.MILLISECONDS);

                if (url == null) return;
                if (visitedUrls.add(url)) {
                    crawl(url);
                }
            } catch (InterruptedException e) {
                log.error("Operation was interrupted {}", e.getMessage());
            }
        }

    }

    private void crawl(URL rootUrl) {

        log.info("Crawling url {}", rootUrl);
        try {
            Document document = Fetcher.fetch(rootUrl.toString());

            List<String> linkedUrls = extractLinkedUrls(document);
            List<URL> linkedUrlsToCrawl = getUrlsToCrawl(rootUrl, linkedUrls);

            addToQueue(linkedUrlsToCrawl);
            addEntryInGraph(rootUrl, linkedUrlsToCrawl);
        } catch (IOException exception) {
            log.error("Unable to reach {}", rootUrl);
        } catch (InvalidInputException exception) {
            log.error("Error while parsing {} url {}", rootUrl, exception.getMessage());
        }

    }

    /*
     * Get all the valid urls which are eligible to crawl
     */
    private List<URL> getUrlsToCrawl(URL rootUrl, List<String> linkedUrls) {
        return linkedUrls.stream()
                .filter(UrlUtil::isValidURL)
                .map(UrlUtil::toURL)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .filter(url -> crawlStrategy.shouldCrawl(rootUrl, url))
                .filter(url -> !visitedUrls.contains(url))
                .collect(Collectors.toList());
    }

    private void addEntryInGraph(URL rootUrl, List<URL> linkedUrls) {
        crawlerGraph.addNode(rootUrl, linkedUrls);
    }

    private void addToQueue(List<URL> linkedUrlsToCrawl) {
        urlsToVisitQueue.addAll(linkedUrlsToCrawl);
    }

    private List<String> extractLinkedUrls(Document document) {
        if (document == null) throw new InvalidInputException("Document should not be null");

        List<String> linkedUrls = new ArrayList<>();
        Elements links = document.select("a[href]");

        for (Element link : links) {
            String url = link.attr("abs:href");
            linkedUrls.add(url);
        }

        return linkedUrls;
    }


}
