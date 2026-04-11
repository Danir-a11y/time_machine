package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class EventResponse {

    private String date;
    private List<HistoricalEvent> historicalEvents;
    private List<BirthEvent> births;
    private List<DeathEvent> deaths;
    private List<MovieDto> movies;
}

