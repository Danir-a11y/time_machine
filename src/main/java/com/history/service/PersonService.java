package com.history.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.history.dto.BirthEvent;
import com.history.dto.DeathEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
@RequiredArgsConstructor
public class PersonService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<BirthEvent> getBirths(LocalDate date, int per_page) {
        try {
            return getBirthsFromWikipedia(date, per_page);
        } catch (Exception e) {
            log.error("Ошибка получения данных о рождениях: {}", e.getMessage(), e);
        }
        return List.of();
    }

    public List<DeathEvent> getDeaths(LocalDate date, int per_page) {
        try {
            return getDeathsFromWikipedia(date, per_page);
        } catch (Exception e) {
            log.error("Ошибка получения данных о смертях: {}", e.getMessage(), e);
        }
        return List.of();
    }

    private List<BirthEvent> getBirthsFromWikipedia(LocalDate date, int per_page) throws Exception {
        String url = "https://api.wikimedia.org/feed/v1/wikipedia/ru/onthisday/births/"
                + date.getMonthValue() + "/" + date.getDayOfMonth();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Не удалось получить данные о рождениях");
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode birthsNode = root.path("births");
        List<BirthEvent> births = new ArrayList<>();

        for (JsonNode birthNode : birthsNode) {
            if (births.size() >= per_page) break;

            int eventYear = birthNode.path("year").asInt();
            if (eventYear != date.getYear()) continue; // фильтр по конкретному году

            BirthEvent birth = new BirthEvent();
            birth.setYear(eventYear);
            birth.setName(extractName(birthNode));
            birth.setOccupation(extractOccupation(birthNode));
            birth.setPhotoUrl(extractPhotoUrl(birthNode));
            birth.setWikiUrl(extractWikiUrl(birthNode));
            births.add(birth);
        }
        return births;
    }

    private List<DeathEvent> getDeathsFromWikipedia(LocalDate date, int per_page) throws Exception {
        String url = "https://api.wikimedia.org/feed/v1/wikipedia/ru/onthisday/deaths/"
                + date.getMonthValue() + "/" + date.getDayOfMonth();

        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);
        if (!response.getStatusCode().is2xxSuccessful() || response.getBody() == null) {
            throw new RuntimeException("Не удалось получить данные о смертях");
        }

        JsonNode root = objectMapper.readTree(response.getBody());
        JsonNode deathsNode = root.path("deaths");
        List<DeathEvent> deaths = new ArrayList<>();

        for (JsonNode deathNode : deathsNode) {
            if (deaths.size() >= per_page) break;

            int eventYear = deathNode.path("year").asInt();
            if (eventYear != date.getYear()) continue; // фильтр по конкретному году

            DeathEvent death = new DeathEvent();
            death.setYear(eventYear);
            death.setName(extractName(deathNode));
            death.setCause(extractOccupation(deathNode));
            death.setPhotoUrl(extractPhotoUrl(deathNode));
            death.setWikiUrl(extractWikiUrl(deathNode));
            deaths.add(death);
        }
        return deaths;
    }

    // ======== вспомогательные методы ========

    private String extractName(JsonNode node) {
        try {
            String text = node.path("text").asText("");
            if (text.contains(" – ")) {
                return text.split(" – ")[0].trim();
            }
            return text;
        } catch (Exception e) {
            log.error("Ошибка извлечения имени: {}", e.getMessage());
            return "Неизвестно";
        }
    }

    private String extractOccupation(JsonNode node) {
        try {
            String text = node.path("text").asText("");
            if (text.contains(" – ")) {
                return text.split(" – ")[1].trim();
            }

            if (node.has("pages") && node.get("pages").isArray() && !node.get("pages").isEmpty()) {
                JsonNode firstPage = node.get("pages").get(0);
                if (firstPage.has("description")) return firstPage.get("description").asText();
                if (firstPage.has("extract")) {
                    String extract = firstPage.get("extract").asText();
                    return extract.contains(".") ? extract.split("\\.")[0] + "." : extract;
                }
            }
        } catch (Exception e) {
            log.error("Ошибка извлечения профессии: {}", e.getMessage());
        }
        return "Известная личность";
    }

    private String extractPhotoUrl(JsonNode node) {
        try {
            if (node.has("pages") && node.get("pages").isArray()) {
                for (JsonNode page : node.get("pages")) {
                    if (page.has("thumbnail")) {
                        JsonNode thumb = page.get("thumbnail");
                        if (thumb.has("source")) return thumb.get("source").asText();
                    }
                    if (page.has("originalimage")) {
                        JsonNode original = page.get("originalimage");
                        if (original.has("source")) return original.get("source").asText();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Ошибка извлечения фото: {}", e.getMessage());
        }
        return null;
    }

    private String extractWikiUrl(JsonNode node) {
        try {
            if (node.has("pages") && node.get("pages").isArray()) {
                for (JsonNode page : node.get("pages")) {

                    String title = page.path("title").asText("");

                    if (title.matches("\\d{3,4}")) continue;

                    if (page.has("content_urls") &&
                            page.get("content_urls").has("desktop") &&
                            page.get("content_urls").get("desktop").has("page")) {

                        return page.get("content_urls").get("desktop").get("page").asText();
                    }
                }
            }
        } catch (Exception e) {
            log.error("Ошибка извлечения ссылки на Википедию: {}", e.getMessage());
        }
        return null;
    }
}

