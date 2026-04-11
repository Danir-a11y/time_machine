package com.history.dto;

import lombok.Data;

@Data
public class BirthEvent {
    private String name;
    private int year;
    private String occupation;
    private String photoUrl;
    private String wikiUrl;
}