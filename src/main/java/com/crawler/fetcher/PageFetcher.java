package com.crawler.fetcher;

import com.crawler.util.URLDepthPair;

import java.util.Map;
import java.util.Queue;
import java.util.Set;

public interface PageFetcher {
    void fetch(URLDepthPair pair, Queue<URLDepthPair> queue, Set<String> visited, Map<String, Set<String>> domainToUrls);
}