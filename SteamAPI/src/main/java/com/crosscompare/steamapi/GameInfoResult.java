package com.crosscompare.steamapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GameInfoResult(
    String id,
    String title,
    String description,
    String releaseDate,
    List<Developer> developers,
    List<Publisher> publishers
) {
    public record Developer(int id, String name) {}
    public record Publisher(int id, String name) {}
}

