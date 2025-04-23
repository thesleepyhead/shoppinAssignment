## Java E-commerce Product URL Crawler

# Overview
This Java-based crawler is designed to extract product page URLS from a list of e-commerce websites. It uses Selenium WebDriver and HtmlUnit for dynamic and static content fetching, respectively. The crawler begins at a set of seed URLS and explores links within the same domain up to a configurable depth.

# Configuration
Global settings are defined in the Config class:

 
public class Config {
    public static final int MAX_DEPTH = 1;                     // Max crawl depth from the seed URL
    public static final int MAX_PRODUCTS_PER_DOMAIN = 50;     // Max product URLs to store per domain *added for testing purpose*
}
# Supported E-Commerce Sites
Initial support is added for:

-Virgio
-TataCliq
-Nykaa Fashion
-Westside

More domains can be added easily to the seed list.

# Project Structure
 
src/
├── com/
│   └── crawler/
│       ├── CrawlerApp.java            # Main crawling logic
│       ├── config/
│       │   └── Config.java            # Global configuration
│       ├── fetcher/
│       │   ├── PageFetcher.java       # Interface for fetchers
│       │   ├── PageFetcherFactory.java# Factory to switch between fetchers
│       │   ├── SeleniumFetcher.java   # Uses Selenium to fetch dynamic pages
│       │   └── HtmlUnitFetcher.java   # Uses HTMLUnit to fetch static content pages
│       └── util/
│           └── URLDepthPair.java      # Custom class to pair URL and depth
# How It Works
Seed Initialization
The application starts from a hardcoded list of seed URLs.

Breadth-First Crawling
Each seed is added to a queue with its depth. URLS are visited breadth-first.

Fetcher Selection

HtmlUnitFetcher is used for static websites (e.g., Nykaa Fashion).

SeleniumFetcher is used for JavaScript-heavy pages (e.g., Virgio, TataCliq).

URL Discovery

Selenium/HTMLUnit scrolls the page and collects all anchor <a> tags.

Only URLS from the same domain are followed.

Product pages are identified via the regex match on paths like /product/, /p/, /products/, etc.

Output
All discovered product URLs are saved to output.txt.

# Product URL Matching Logic
Inside SeleniumFetcher.java & HtmlUnitFetcher.java:

 
return url.matches(".*/(products?|items?|p)/[^/?#]+.*");
This regex captures URLs like:

/products/sku-name

/product/sku-id

/p/some-product

# Design Decisions
The Factory Pattern is used to select the fetcher type dynamically.

Breadth-first search ensures the closest relevant pages are prioritised.

Set for visited URLS ensures no page is fetched more than once.

Domain-scoped crawling restricts traversal to within the same website.

# Sample Output
 
Domain: www.virgio.com | Products found: 12
  https://www.virgio.com/products/veronicas-playful-cotton-checked-skirt
  https://www.virgio.com/products/daisy-denim-jacket
  ...
# How to Run
Ensure Java and ChromeDriver are installed.

Add Selenium to the classpath.

Run the main class:

java com.crawler.CrawlerApp