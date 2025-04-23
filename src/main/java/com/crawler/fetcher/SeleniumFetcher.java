package com.crawler.fetcher;

import com.crawler.util.URLDepthPair;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.*;
import org.openqa.selenium.support.ui.*;
import java.net.URL;
import com.crawler.config.Config;

import java.time.Duration;
import java.util.*;

public class SeleniumFetcher implements PageFetcher {

    @Override
    public void fetch(URLDepthPair pair, Queue<URLDepthPair> queue,
                      Set<String> visited, Map<String, Set<String>> domainToProductUrls) {

        System.out.println(">>> [Selenium] Fetching: " + pair.getUrl());
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless", "--disable-gpu", "--no-sandbox");

        WebDriver driver = null;

        try {
            driver = new ChromeDriver(options);
            driver.get(pair.getUrl());
            driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(15));
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));

            new WebDriverWait(driver, Duration.ofSeconds(10)).until(
                wd -> ((JavascriptExecutor) wd).executeScript("return document.readyState").equals("complete")
            );

            for (int i = 0; i < 3; i++) {
                ((JavascriptExecutor) driver).executeScript("window.scrollTo(0, document.body.scrollHeight);");
                Thread.sleep(1000);
            }

            new WebDriverWait(driver, Duration.ofSeconds(10))
                .until(ExpectedConditions.presenceOfElementLocated(By.tagName("a")));

            List<WebElement> anchors = driver.findElements(By.tagName("a"));

            for (WebElement anchor : anchors) {
                String href = anchor.getAttribute("href");
                if (href == null || href.isEmpty() || visited.contains(href)) continue;
                if (href.contains("login") || href.contains("signup") || href.contains("cart") ||
    href.contains("track") || href.contains("help") || href.contains("faq") || href.contains("apps")) {
    continue;
}


                domainToProductUrls.computeIfAbsent(pair.getSeedDomain(), k -> new HashSet<>());

                if (isProductUrl(href)) {
                    domainToProductUrls.get(pair.getSeedDomain()).add(href);
                    System.out.println("[Selenium] Product URL found: " + href);
                }
                if (isSameDomain(pair.getSeedDomain(), href) && !visited.contains(href) && ((pair.getDepth()+1) <= Config.MAX_DEPTH)) {
                    queue.add(new URLDepthPair(href, pair.getDepth() + 1, pair.getSeedDomain()));
                }
            }
        } catch (Exception e) {
            System.out.println("[Selenium] Exception fetching " + pair.getUrl());
            e.printStackTrace();
        } finally {
            if (driver != null) driver.quit();
        }
    }

    private boolean isProductUrl(String url) {
        if (url == null) return false;
    
        url = url.toLowerCase();
    
        // Exclude known social or irrelevant domains
        if (url.contains("instagram.com") || url.contains("youtube.com") || url.contains("facebook.com")) {
            return false;
        }
    
        // Match only if the path contains /product/, /p/, or /item/
        return url.matches(".*/(products?|items?|p)/[^/?#]+.*");
    }
    
    
    private boolean isSameDomain(String seedDomain, String url) {
        try {
            String host = new URL(url).getHost();
            return host.endsWith(seedDomain);
        } catch (Exception e) {
            return false;
        }
    }
    
}
