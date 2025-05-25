package com.crosscompare.steamapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GameSearchResult(String id, String title, Assets assets) {
    @JsonIgnoreProperties(ignoreUnknown = true)
    public record Assets(String boxart) {}
}
