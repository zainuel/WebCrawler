package com.crawler.handler;

import com.crawler.crawl.strategy.CrawlStrategy;
import com.crawler.crawl.strategy.SameDomainCrawlStrategy;
import com.crawler.crawler.Crawler;
import com.crawler.model.Graph;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;

@Slf4j
public class CrawlerExecutor {

    private final int noOfCrawlers;
    private final URL rootUrl;
    private final Set<URL> visitedUrls;
    private final BlockingQueue<URL> urlsToVisitQueue;
    private final ExecutorService executorService;
    private final Graph crawlerGraph;
    private final CrawlStrategy crawlStrategy;

    public CrawlerExecutor(final URL rootUrl, int noOfCrawlers) {
        this.noOfCrawlers = noOfCrawlers;
        this.rootUrl = rootUrl;
        this.executorService = Executors.newFixedThreadPool(noOfCrawlers);
        this.crawlerGraph = new Graph();
        this.urlsToVisitQueue = new LinkedBlockingQueue<>();
        this.visitedUrls = ConcurrentHashMap.newKeySet();
        this.crawlStrategy = new SameDomainCrawlStrategy();
    }

    public Graph execute() {
        log.info("Crawling URL {} with {} workers", rootUrl, noOfCrawlers);

        List<CompletableFuture<?>> futures = new ArrayList<>();
        urlsToVisitQueue.add(rootUrl);

        for (int i = 0; i < noOfCrawlers; i++) {
            futures.add(CompletableFuture.runAsync(buildRunnableCrawler(), executorService));
        }

        CompletableFuture.allOf(futures.stream().toArray(CompletableFuture[]::new)).join();

        return this.crawlerGraph;

    }

    private Crawler buildRunnableCrawler() {
        return Crawler.builder()
                .rootUrl(rootUrl)
                .urlsToVisitQueue(urlsToVisitQueue)
                .crawlerGraph(crawlerGraph)
                .visitedUrls(visitedUrls)
                .crawlStrategy(crawlStrategy)
                .build();
    }

    public void shutdown() {
        executorService.shutdown();
    }


}
