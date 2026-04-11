package com.history.controller;

import com.history.dto.EventResponse;
import com.history.service.HistoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;


@RestController
@RequestMapping("/history")
@Tag(name = "Исторические события", description = "API для получения событий по дате")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping("/events")
    @Operation(
            summary = "Получить события по дате",
            description = "Возвращает исторические события, релизы песен и фильмов для указанной даты"
    )
    public ResponseEntity<EventResponse> getEventsByDate(
            @Parameter(description = "Дата в формате YYYY-MM-DD", example = "1999-12-31")
            @RequestParam
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate date,
            @RequestParam(defaultValue = "5") int per_page) {

        EventResponse response = historyService.getEventsByDate(date, per_page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/events/today")
    @Operation(summary = "События сегодняшнего дня")
    public ResponseEntity<EventResponse> getTodayEvents(
            @RequestParam(defaultValue = "5") int per_page) {
        EventResponse response = historyService.getEventsByDate(LocalDate.now(), per_page);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/health")
    public ResponseEntity<String> health() {
        return ResponseEntity.ok("Historical Events API is running!");
    }
}