package com.history.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class DeathEvent {
    private String name;
    private int year;
    private String cause;
    private String photoUrl;
    private String wikiUrl;
}