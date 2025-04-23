package com.crawler;

import com.crawler.fetcher.PageFetcher;
import com.crawler.fetcher.PageFetcherFactory;
import com.crawler.util.URLDepthPair;
import com.crawler.config.Config;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.URL;
import java.util.*;

public class CrawlerApp {
    public static void main(String[] args) {
        // 1) Fixed list of seeds
        List<String> seeds = Arrays.asList(
            "https://www.virgio.com/",
            "https://www.tatacliq.com/",
            "https://nykaafashion.com/",
            "https://www.westside.com/"
        );

        Set<String> visited = new HashSet<>();
        Queue<URLDepthPair> queue = new LinkedList<>();
        Map<String, Set<String>> domainToUrls = new HashMap<>();

        // 2) Initialize queue with seed URLDepthPairs
        for (String url : seeds) {
            String domain = extractDomain(url);
            queue.add(new URLDepthPair(url, 0, domain));
            domainToUrls.put(domain, new HashSet<>());  // prepare map entry
        }

        // 3) Crawl loop
        while (!queue.isEmpty()) {
            System.out.println("Queue size: "+ queue.size());
            URLDepthPair pair = queue.poll();
            String url = pair.getUrl();
            String domain = pair.getSeedDomain();

            // skip already visited or too deep
            System.out.println("Fetching: " + pair.getUrl() + "depth:" +pair.getDepth());
            if (visited.contains(url) || pair.getDepth() > Config.MAX_DEPTH) continue;
            visited.add(url);

            // 4) Choose fetcher per-domain
            PageFetcher fetcher = url.contains("nykaafashion.com")
                ? PageFetcherFactory.getFetcher("htmlunit")
                : PageFetcherFactory.getFetcher("selenium");
                System.out.println("Fetcher type: " + fetcher.getClass().getSimpleName());
                long start = System.currentTimeMillis();
            // 5) Fetch links, populate domainToUrls and queue
            fetcher.fetch(pair, queue, visited, domainToUrls);
            System.out.println("Fetched: " + pair.getUrl() + " in " + (System.currentTimeMillis() - start) + " ms");
        }

        // 6) Write output
        try (BufferedWriter writer = new BufferedWriter(new FileWriter("output.txt"))) {
            for (var entry : domainToUrls.entrySet()) {
                writer.write("Domain: " + entry.getKey());
                writer.newLine();
                for (String productUrl : entry.getValue()) {
                    writer.write("  - " + productUrl);
                    writer.newLine();
                }
                writer.newLine();
            }
            System.out.println("Crawling complete. See output.txt");
            System.out.println("\n=== Crawling complete ===");
for (Map.Entry<String, Set<String>> entry : domainToUrls.entrySet()) {
    System.out.println("Domain: " + entry.getKey() + " | Products found: " + entry.getValue().size());
    for (String url : entry.getValue()) {
        System.out.println("  " + url);
    }
}

        } catch (IOException e) {
            System.err.println("Error writing output: " + e.getMessage());
        }
    }

    private static String extractDomain(String url) {
        try {
            return new URL(url).getHost();
        } catch (Exception e) {
            return url;
        }
    }
}
