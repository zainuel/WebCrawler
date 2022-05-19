package com.crawler.model;

import lombok.Getter;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class Graph {
    /*
     * Store Parent URL as the key and all the linked URLs found in the parent URL as the value
     */
    private final Map<URL, List<URL>> nodeToEdgesMap;

    public Graph() {
        nodeToEdgesMap = new ConcurrentHashMap<>();
    }

    public void addNode(URL node, List<URL> edges) {
        nodeToEdgesMap.putIfAbsent(node, new ArrayList<>(edges));
    }

    public boolean contains(URL node) {
        return nodeToEdgesMap.containsKey(node);
    }

    public List<URL> get(URL node) {
        return nodeToEdgesMap.get(node);
    }

}
