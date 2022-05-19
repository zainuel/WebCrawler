package com.crawler.fetcher;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;

import java.io.IOException;

@Slf4j
public class Fetcher {

    private Fetcher() {
    }

    public static Document fetch(String url) throws IOException {
        return Jsoup.connect(url).get();
    }
}
