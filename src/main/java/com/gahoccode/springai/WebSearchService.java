package com.gahoccode.springai;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class WebSearchService {

    private final WebClient webClient;

    public WebSearchService() {
        this.webClient = WebClient.builder()
                .defaultHeader("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                .build();
    }

    /**
     * Tìm kiếm thông tin trên Google
     */
    public String searchGoogle(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://www.google.com/search?q=" + encodedQuery + "&hl=vi";

            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36")
                    .timeout(5000)
                    .get();

            // Lấy featured snippet (nếu có)
            Element featuredSnippet = doc.selectFirst("div[data-attrid='FeaturedSnippet']");
            if (featuredSnippet != null) {
                return featuredSnippet.text();
            }

            // Lấy knowledge panel
            Element knowledgePanel = doc.selectFirst("div.kp-wholepage");
            if (knowledgePanel != null) {
                return knowledgePanel.text().substring(0, Math.min(500, knowledgePanel.text().length()));
            }

            // Lấy top search results
            StringBuilder results = new StringBuilder();
            var searchResults = doc.select("div.g");
            int count = 0;

            for (Element result : searchResults) {
                if (count >= 3) break;

                Element snippet = result.selectFirst("div[data-sncf], div.VwiC3b");
                if (snippet != null) {
                    results.append(snippet.text()).append(" ");
                    count++;
                }
            }

            return results.length() > 0 ? results.toString().trim() : null;

        } catch (Exception e) {
            log.error("Error searching Google: ", e);
            return null;
        }
    }

    /**
     * Tìm kiếm trên Wikipedia tiếng Việt
     */
    public String searchWikipedia(String query) {
        try {
            String encodedQuery = URLEncoder.encode(query, StandardCharsets.UTF_8);
            String url = "https://vi.wikipedia.org/w/api.php?action=query&format=json&prop=extracts&exintro=1&explaintext=1&titles=" + encodedQuery;

            String response = webClient.get()
                    .uri(url)
                    .retrieve()
                    .bodyToMono(String.class)
                    .block();

            if (response != null && response.contains("\"extract\":")) {
                int start = response.indexOf("\"extract\":\"") + 11;
                int end = response.indexOf("\"", start);
                String extract = response.substring(start, end);

                // Giới hạn độ dài
                return extract.length() > 800 ? extract.substring(0, 800) + "..." : extract;
            }

            return null;

        } catch (Exception e) {
            log.error("Error searching Wikipedia: ", e);
            return null;
        }
    }

    /**
     * Tìm kiếm thông tin tổng hợp
     */
    public String search(String query) {
        log.info("Searching web for: {}", query);

        // Thử Wikipedia trước (nhanh hơn và đáng tin cậy)
        String wikiResult = searchWikipedia(query);
        if (wikiResult != null && !wikiResult.trim().isEmpty()) {
            log.info("Found result from Wikipedia");
            return wikiResult;
        }

        // Nếu không có thì tìm Google
        String googleResult = searchGoogle(query);
        if (googleResult != null && !googleResult.trim().isEmpty()) {
            log.info("Found result from Google");
            return googleResult;
        }

        return null;
    }
}