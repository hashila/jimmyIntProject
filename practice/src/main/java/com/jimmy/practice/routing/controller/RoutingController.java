package com.jimmy.practice.routing.controller;

import com.jimmy.practice.routing.model.RouteResponse;
import com.jimmy.practice.routing.service.RoutingService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/routing")
public class RoutingController {

    private final RoutingService routingService;

    public RoutingController(RoutingService routingService) {
        this.routingService = routingService;
    }

    @GetMapping("/{origin}/{destination}")
    public RouteResponse getRoute(@PathVariable String origin, @PathVariable String destination) {
        List<String> route = routingService.findLandRoute(origin, destination);
        return new RouteResponse(route);
    }
}

