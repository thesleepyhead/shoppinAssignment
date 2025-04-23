package com.crawler.fetcher;

import com.crawler.util.URLDepthPair;
import com.gargoylesoftware.htmlunit.*;
import com.gargoylesoftware.htmlunit.html.*;
import com.crawler.config.Config;

import java.net.URL;
import java.util.*;

public class HtmlUnitFetcher implements PageFetcher {

    @Override
    public void fetch(URLDepthPair pair, Queue<URLDepthPair> queue,
                      Set<String> visited, Map<String, Set<String>> domainToProductUrls) {
        System.out.println(">>> [HtmlUnit] Fetching: " + pair.getUrl());
        try (final WebClient client = new WebClient(BrowserVersion.BEST_SUPPORTED)) {
            client.getOptions().setCssEnabled(false);
            client.getOptions().setJavaScriptEnabled(true);
            client.getOptions().setThrowExceptionOnScriptError(false);
            client.getOptions().setTimeout(15000); // 15 seconds
            client.waitForBackgroundJavaScript(5000); // adjust as needed
            
            HtmlPage page = client.getPage(pair.getUrl());
            client.waitForBackgroundJavaScript(5000);

            List<HtmlAnchor> anchors = page.getAnchors();

            for (HtmlAnchor anchor : anchors) {
                String href = anchor.getHrefAttribute();
                if (href == null || href.isEmpty() || href.startsWith("#")) continue;
                if (href.contains("login") || href.contains("signup") || href.contains("cart") ||
    href.contains("track") || href.contains("help") || href.contains("faq") || href.contains("apps")) {
    continue;
}


                String absUrl = new URL(new URL(pair.getUrl()), href).toString();
                if (visited.contains(absUrl)) continue;

                domainToProductUrls.computeIfAbsent(pair.getSeedDomain(), k -> new HashSet<>());

                if (isProductUrl(absUrl)) {
                    domainToProductUrls.get(pair.getSeedDomain()).add(absUrl);
                    System.out.println("[HtmlUnit] Product URL found: " + absUrl);
                }

                if (isSameDomain(pair.getSeedDomain(), absUrl) && !visited.contains(absUrl) && ((pair.getDepth()+1) <= Config.MAX_DEPTH)) {
                    queue.add(new URLDepthPair(absUrl, pair.getDepth() + 1, pair.getSeedDomain()));
                }
            }
        } catch (Exception e) {
            System.out.println("[HtmlUnit] Exception fetching " + pair.getUrl());
            e.printStackTrace();
        }
    }

    private boolean isProductUrl(String url) {
        if (url == null) return false;
    
        url = url.toLowerCase();
    // Exclude known social or irrelevant domains
    if (url.contains("instagram.com") || url.contains("youtube.com") || url.contains("facebook.com")) {
        return false;
    }

        // Match common product URL patterns
return url.matches(".*/(products?|items?|p)/[^/?#]+.*");
}
    

    private boolean isSameDomain(String seedDomain, String url) {
        return url.contains(seedDomain);
    }
}
