package com.crosscompare.steamapi;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public record GamePriceResult(
    Deal[] deals
) {
    public record Deal(Price price) {}
    public record Price(double amount, String currency) {}
}

