package com.crawler.util;

public class URLDepthPair {
    private final String url;
    private final int depth;
    private final String seedDomain;

    public URLDepthPair(String url, int depth, String seedDomain) {
        this.url = url;
        this.depth = depth;
        this.seedDomain = seedDomain;
    }

    public String getUrl() {
        return url;
    }

    public int getDepth() {
        return depth;
    }

    public String getSeedDomain() {
        return seedDomain;
    }
}