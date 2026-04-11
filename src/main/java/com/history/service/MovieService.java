package com.history.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.history.dto.MovieDto;
import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MovieService {

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public List<MovieDto> getMovies(LocalDate date, int perPage) {

        String year = String.valueOf(date.getYear());
        String month = date.getMonth().name();

        String url = String.format(
                "https://kinopoiskapiunofficial.tech/api/v2.2/films/premieres?year=%s&month=%s",
                year, month
        );

        try {
            HttpHeaders headers = new HttpHeaders();
            String apiKey = "87d86ed7-7dda-46f6-a747-e0a5bc314ec3";
            headers.set("X-API-KEY", apiKey);
            HttpEntity<String> entity = new HttpEntity<>(headers);

            ResponseEntity<String> response = restTemplate.exchange(
                    url,
                    HttpMethod.GET,
                    entity,
                    String.class
            );

            String json = response.getBody();
            if (json == null) return List.of();

            JsonNode root = objectMapper.readTree(json);
            JsonNode items = root.path("items");

            List<MovieDto> movies = new ArrayList<>();

            for (JsonNode item : items) {
                if (movies.size() >= perPage) break;

                String premiereRu = item.path("premiereRu").asText(null);
                if (premiereRu == null) continue;

                LocalDate premiereDate = LocalDate.parse(premiereRu);

                if (!premiereDate.equals(date)) continue;

                String name = firstNonNull(
                        item.path("nameRu").asText(null),
                        item.path("nameEn").asText(null),
                        item.path("nameOriginal").asText(null)
                );

                String photoUrl = item.path("posterUrl").asText(null);

                List<String> genres = new ArrayList<>();
                for (JsonNode genreNode : item.path("genres")) {
                    genres.add(genreNode.path("genre").asText());
                }

                // 🔹 ДЛИТЕЛЬНОСТЬ
                int duration = item.path("duration").asInt(0);
                int kinopoiskId = item.path("kinopoiskId").asInt(0);
                movies.add(new MovieDto(
                        kinopoiskId,
                        name,
                        photoUrl,
                        genres,
                        duration
                ));
            }

            return movies;

        } catch (Exception e) {
            e.printStackTrace();
            return List.of();
        }
    }

    private String firstNonNull(String... values) {
        for (String v : values) {
            if (v != null && !v.isEmpty()) return v;
        }
        return "Unknown";
    }

    public List<MovieDto> getLocalMovies(LocalDate date) {
        return List.of(); // TODO локальные данные
    }
}
