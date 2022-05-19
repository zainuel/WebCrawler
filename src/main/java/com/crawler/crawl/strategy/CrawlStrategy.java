package com.crawler.crawl.strategy;

import java.net.URL;

public interface CrawlStrategy {

    boolean shouldCrawl(URL rootUrl, URL linkedUrl);
}
