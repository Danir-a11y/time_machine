package com.history.service;

import com.history.dto.EventResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Service
@RequiredArgsConstructor
public class HistoryService {

    private final HistoricalEventService historicalEventService;
    private final PersonService personService;
    private final MovieService movieService;

    @Cacheable(value = "events", key = "#date")
    public EventResponse getEventsByDate(LocalDate date, int per_page) {

        EventResponse response = new EventResponse();
        response.setDate(date.format(DateTimeFormatter.ISO_DATE));

        try {
            response.setHistoricalEvents(
                    historicalEventService.getEvents(date, per_page));
            response.setBirths(
                    personService.getBirths(date, per_page));
            response.setDeaths(
                    personService.getDeaths(date, per_page));
            response.setMovies(
                    movieService.getMovies(date, per_page));


        } catch (Exception ignored) {
        }

        return response;
    }


}
