package com.crawler.fetcher;

public class PageFetcherFactory {
    public static PageFetcher getFetcher(String type) {
        if ("selenium".equalsIgnoreCase(type)) {
            return new SeleniumFetcher();
        } else {
            return new HtmlUnitFetcher();
        }
    }
}