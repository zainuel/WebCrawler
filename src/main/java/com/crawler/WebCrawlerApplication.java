package com.crawler;

import com.crawler.exceptions.InvalidInputException;
import com.crawler.handler.CrawlerExecutor;
import com.crawler.model.Graph;
import com.crawler.printer.FilePrinter;
import com.crawler.printer.GraphPrinter;
import lombok.extern.slf4j.Slf4j;

import java.net.URL;
import java.util.Optional;

import static com.crawler.utils.UrlUtil.toURL;

@Slf4j
public class WebCrawlerApplication {

    public static void main(String[] args) {
        if (args.length < 2) throw new InvalidInputException("Provide valid no of crawlers and an URL to Crawl");

        try {
            Optional<URL> optionalURL = toURL(args[0]);
            if (!optionalURL.isPresent()) throw new InvalidInputException("Invalid URL passed");
            int noOfCrawlers = parseInteger(args[1]);
            GraphPrinter printer = new FilePrinter();

            CrawlerExecutor crawlerExecutor = new CrawlerExecutor(optionalURL.get(), noOfCrawlers);
            Graph graph = crawlerExecutor.execute();
            printer.print(graph);
            crawlerExecutor.shutdown();

        } catch (NumberFormatException e) {
            log.error("Error while parsing input {} ", e);
            throw new InvalidInputException("Provide valid no of crawlers to Crawl");
        }

        log.info("Crawling is complete.");
    }

    private static int parseInteger(String arg) {
        return Integer.parseInt(arg);
    }

}
