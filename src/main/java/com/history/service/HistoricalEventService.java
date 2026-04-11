package com.history.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.history.dto.HistoricalEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.*;

@Service
@Slf4j
@RequiredArgsConstructor
public class HistoricalEventService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<HistoricalEvent> getEvents(LocalDate date, int per_page) {
        try {
            return getEventsFromWikipedia(date, per_page);
        } catch (Exception e) {
            log.error("Ошибка получения исторических событий: {}", e.getMessage());
        }
        return List.of();
    }

    private List<HistoricalEvent> getEventsFromWikipedia(LocalDate date, int per_page) throws JsonProcessingException {
        String month = String.format("%02d", date.getMonthValue());
        String day = String.format("%02d", date.getDayOfMonth());

        String url = "https://ru.wikipedia.org/api/rest_v1/feed/onthisday/events/" + month + "/" + day;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Не удалось получить данные из Wikipedia API");
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode eventsNode = root.path("events");

        List<HistoricalEvent> events = new ArrayList<>();
        int targetYear = date.getYear();

        for (JsonNode eventNode : eventsNode) {
            int eventYear = eventNode.path("year").asInt();

            if (eventYear == targetYear) {
                HistoricalEvent event = new HistoricalEvent();
                event.setYear(eventYear);

                String text = eventNode.path("text").asText();
                if (!text.isEmpty()) {
                    text = text.substring(0, 1).toUpperCase(new Locale("ru")) + text.substring(1);
                }
                event.setDescription(text);
                event.setCategories(extractCategories(eventNode));

                // Берем вторую страницу (если есть) — это статья события
                JsonNode pageNode = null;
                if (eventNode.has("pages") && eventNode.get("pages").isArray() && eventNode.get("pages").size() > 1) {
                    pageNode = eventNode.get("pages").get(1); // вторая страница
                } else if (eventNode.has("pages") && eventNode.get("pages").size() == 1) {
                    pageNode = eventNode.get("pages").get(0); // fallback
                }

                if (pageNode != null) {
                    if (pageNode.has("thumbnail")) {
                        event.setPhotoUrl(pageNode.path("thumbnail").path("source").asText(null));
                    }
                    if (pageNode.has("content_urls")) {
                        event.setWikiUrl(pageNode.path("content_urls").path("desktop").path("page").asText(null));
                    }
                    if (pageNode.has("extract")) {
                        event.setExtract(pageNode.path("extract").asText(null));
                    }
                }

                if (events.size() < per_page) events.add(event);
            }
        }

        return events;
    }

    private List<String> extractCategories(JsonNode eventNode) {
        List<String> categories = new ArrayList<>();
        if (eventNode.has("pages")) {
            eventNode.path("pages").forEach(page -> {
                if (page.has("description")) {
                    String description = page.path("description").asText();
                    if (!description.isEmpty()) {
                        description = description.substring(0, 1).toUpperCase(new Locale("ru")) + description.substring(1);
                    }
                    categories.add(description);
                }
            });
        }
        return categories;
    }
}

