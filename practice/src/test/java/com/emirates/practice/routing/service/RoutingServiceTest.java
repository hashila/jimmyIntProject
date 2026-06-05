package com.jimmy.practice.routing.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import com.jimmy.practice.routing.client.CountryGraphProvider;
import com.jimmy.practice.routing.exception.NoLandRouteException;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class RoutingServiceTest {

    private RoutingService routingService;

    @BeforeEach
    void setUp() {
        CountryGraphProvider countryGraphProvider = () -> Map.of(
                "CZE", Set.of("AUT", "POL"),
                "AUT", Set.of("CZE", "ITA"),
                "POL", Set.of("CZE"),
                "ITA", Set.of("AUT"),
                "ISL", Set.of());

        routingService = new RoutingService(countryGraphProvider);
    }

    @Test
    void shouldReturnShortestPath() {
        List<String> route = routingService.findLandRoute("cze", "ita");

        assertEquals(List.of("CZE", "AUT", "ITA"), route);
    }

    @Test
    void shouldReturnSingleCountryWhenSameOriginAndDestination() {
        List<String> route = routingService.findLandRoute("ita", "ITA");

        assertEquals(List.of("ITA"), route);
    }

    @Test
    void shouldThrowWhenNoLandRouteExists() {
        assertThrows(NoLandRouteException.class, () -> routingService.findLandRoute("CZE", "ISL"));
    }

    @Test
    void shouldThrowWhenCountryCodeUnknown() {
        assertThrows(NoLandRouteException.class, () -> routingService.findLandRoute("AAA", "ITA"));
    }
}

