package com.crawler.printer;


import com.crawler.model.Graph;
import org.apache.commons.io.FileUtils;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

class FilePrinterTest {


    @Test
    void shouldWriteTheOutputToFile() throws IOException {
        Graph graph = buildGraph();
        FilePrinter filePrinter = new FilePrinter();

        filePrinter.print(graph);

        File file1 = new File("src/test/resources/graph_test_output.txt");
        File file2 = new File("output.txt");

        Assertions.assertTrue(FileUtils.contentEquals(file1, file2));


    }

    private Graph buildGraph() throws MalformedURLException {
        Graph graph = new Graph();
        URL url1 = new URL("http://google.com");
        URL url2 = new URL("http://google.com/support");
        URL url3 = new URL("http://google.com/community");
        URL url4 = new URL("http://google.com/help");
        URL url5 = new URL("http://google.com/contact");

        List<URL> linkedUrls1 = new ArrayList<>();
        linkedUrls1.add(url2);
        linkedUrls1.add(url3);
        linkedUrls1.add(url4);

        List<URL> linkedUrls2 = new ArrayList<>();
        linkedUrls2.add(url5);

        graph.addNode(url1, linkedUrls1);
        graph.addNode(url4, linkedUrls2);
        return graph;
    }

}