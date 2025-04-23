package com.crawler.crawler;

import com.crawler.fetcher.PageFetcher;
import com.crawler.util.URLDepthPair;

import java.util.*;

public class ProductCrawler {

    private final PageFetcher fetcher;

    public ProductCrawler(PageFetcher fetcher) {
        this.fetcher = fetcher;
    }

    public Map<String, Set<String>> crawl(URLDepthPair seed, int maxDepth) {
        Queue<URLDepthPair> queue = new LinkedList<>();
        Set<String> visited = new HashSet<>();
        Map<String, Set<String>> domainToUrls = new HashMap<>();

        queue.add(seed);
        visited.add(seed.getUrl());

        while (!queue.isEmpty()) {
            URLDepthPair pair = queue.poll();
            if (pair.getDepth() > maxDepth) continue;
            fetcher.fetch(pair, queue, visited, domainToUrls);
        }

        return domainToUrls;
    }
}