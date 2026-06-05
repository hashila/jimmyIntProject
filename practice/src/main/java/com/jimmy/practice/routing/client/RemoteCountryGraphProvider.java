package com.jimmy.practice.routing.client;

import com.jimmy.practice.routing.exception.NoLandRouteException;
import com.jimmy.practice.routing.model.CountryData;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.json.JsonParser;
import org.springframework.boot.json.JsonParserFactory;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
public class RemoteCountryGraphProvider implements CountryGraphProvider {

    private final RestClient restClient;
    private final String countriesDataUrl;
    private volatile Map<String, Set<String>> graphCache;

    public RemoteCountryGraphProvider(
            RestClient.Builder restClientBuilder,
            @Value("${routing.countries-data-url:https://raw.githubusercontent.com/mledoze/countries/master/countries.json}")
            String countriesDataUrl) {
        this.restClient = restClientBuilder.build();
        this.countriesDataUrl = countriesDataUrl;
    }

    @Override
    public Map<String, Set<String>> getCountryBorderGraph() {
        Map<String, Set<String>> snapshot = graphCache;
        if (snapshot != null) {
            return snapshot;
        }

        synchronized (this) {
            if (graphCache == null) {
                graphCache = Collections.unmodifiableMap(fetchAndBuildGraph());
            }
            return graphCache;
        }
    }

    private Map<String, Set<String>> fetchAndBuildGraph() {
        try {
            String payload = restClient.get()
                    .uri(countriesDataUrl)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(String.class);

            List<CountryData> countries = parseCountries(payload);

            Set<String> validCodes = new HashSet<>();
            for (CountryData country : countries) {
                if (country.cca3() != null) {
                    validCodes.add(country.cca3().toUpperCase(Locale.ROOT));
                }
            }

            Map<String, Set<String>> graph = new HashMap<>();
            for (String code : validCodes) {
                graph.put(code, new HashSet<>());
            }

            for (CountryData country : countries) {
                if (country.cca3() == null) {
                    continue;
                }
                String countryCode = country.cca3().toUpperCase(Locale.ROOT);
                List<String> borders = country.borders() == null ? Collections.emptyList() : country.borders();
                for (String borderCode : borders) {
                    String normalizedBorder = borderCode.toUpperCase(Locale.ROOT);
                    if (!validCodes.contains(normalizedBorder)) {
                        continue;
                    }
                    graph.get(countryCode).add(normalizedBorder);
                    graph.get(normalizedBorder).add(countryCode);
                }
            }

            return graph;
        } catch (Exception ex) {
            throw new NoLandRouteException("Unable to load countries data");
        }
    }

    private List<CountryData> parseCountries(String payload) {
        if (payload == null || payload.isBlank()) {
            return Collections.emptyList();
        }

        JsonParser jsonParser = JsonParserFactory.getJsonParser();
        List<Object> parsedCountries = jsonParser.parseList(payload);
        List<CountryData> countries = new ArrayList<>(parsedCountries.size());

        for (Object entry : parsedCountries) {
            if (!(entry instanceof Map<?, ?> countryMap)) {
                continue;
            }

            Object rawCode = countryMap.get("cca3");
            if (!(rawCode instanceof String code) || code.isBlank()) {
                continue;
            }

            List<String> borders = new ArrayList<>();
            Object rawBorders = countryMap.get("borders");
            if (rawBorders instanceof List<?> borderList) {
                for (Object border : borderList) {
                    if (border instanceof String borderCode && !borderCode.isBlank()) {
                        borders.add(borderCode);
                    }
                }
            }

            countries.add(new CountryData(code, borders));
        }

        return countries;
    }
}
