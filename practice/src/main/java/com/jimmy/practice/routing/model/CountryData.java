package com.jimmy.practice.routing.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public record CountryData(String cca3, List<String> borders) {
}

