package com.jimmy.practice.routing.controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

import com.jimmy.practice.routing.exception.NoLandRouteException;
import com.jimmy.practice.routing.model.RouteResponse;
import com.jimmy.practice.routing.service.RoutingService;
import java.util.List;
import org.junit.jupiter.api.Test;

class RoutingControllerTest {

    private final RoutingService routingService = mock(RoutingService.class);
    private final RoutingController routingController = new RoutingController(routingService);

    @Test
    void shouldReturnRoute() {
        given(routingService.findLandRoute("CZE", "ITA")).willReturn(List.of("CZE", "AUT", "ITA"));

        RouteResponse response = routingController.getRoute("CZE", "ITA");

        assertEquals(List.of("CZE", "AUT", "ITA"), response.route());
    }

    @Test
    void shouldThrowWhenRouteDoesNotExist() {
        given(routingService.findLandRoute("CZE", "ISL")).willThrow(new NoLandRouteException("No land route found"));

        assertThrows(NoLandRouteException.class, () -> routingController.getRoute("CZE", "ISL"));
    }
}


