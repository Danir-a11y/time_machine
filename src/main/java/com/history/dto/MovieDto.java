package com.history.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MovieDto {
    private int kinopoiskId;
    private String name;
    private String photoUrl;
    private List<String> genres;
    private int duration;
}

