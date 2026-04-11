package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class HistoricalEvent {
    private int year;
    private String description;
    private List<String> categories;
    private String photoUrl;
    private String wikiUrl;
    private String extract;
}
