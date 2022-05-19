package com.crawler.crawl.strategy;

import com.crawler.utils.UrlUtil;

import java.net.URL;

public class SameDomainCrawlStrategy implements CrawlStrategy {

    @Override
    public boolean shouldCrawl(URL rootUrl, URL linkedUrl) {
        return UrlUtil.isSameHost(rootUrl, linkedUrl);
    }
}
