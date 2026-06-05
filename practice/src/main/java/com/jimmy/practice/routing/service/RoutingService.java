package com.jimmy.practice.routing.service;

import com.jimmy.practice.routing.client.CountryGraphProvider;
import com.jimmy.practice.routing.exception.NoLandRouteException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Deque;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import org.springframework.stereotype.Service;

@Service
public class RoutingService {

    private final CountryGraphProvider countryGraphProvider;

    public RoutingService(CountryGraphProvider countryGraphProvider) {
        this.countryGraphProvider = countryGraphProvider;
    }

    
    public List<String> findLandRoute(String origin, String destination) {
        String start = normalize(origin);
        String target = normalize(destination);

        Map<String, Set<String>> graph = countryGraphProvider.getCountryBorderGraph();
        if (!graph.containsKey(start) || !graph.containsKey(target)) {
            throw new NoLandRouteException("Unknown country code");
        }

        if (start.equals(target)) {
            return List.of(start);
        }
        return bfsShortestPath(graph, start, target);
    }

    private List<String> bfsShortestPath(Map<String, Set<String>> graph, String start, String target) {
        Deque<String> queue = new ArrayDeque<>();
        Set<String> visited = new HashSet<>();
        Map<String, String> predecessor = new HashMap<>();

        queue.add(start);
        visited.add(start);

        while (!queue.isEmpty()) {
            String current = queue.removeFirst();
            for (String neighbor : graph.getOrDefault(current, Collections.emptySet())) {
                if (visited.contains(neighbor)) {
                    continue;
                }

                visited.add(neighbor);
                predecessor.put(neighbor, current);

                if (neighbor.equals(target)) {
                    return constructPath(predecessor, start, target);
                }

                queue.addLast(neighbor);
            }
        }

        throw new NoLandRouteException("No land route found");
    }

    private List<String> constructPath(Map<String, String> predecessor, String start, String target) {
        List<String> path = new ArrayList<>();
        String step = target;
        while (step != null) {
            path.add(step);
            if (step.equals(start)) {
                break;
            }
            step = predecessor.get(step);
        }

        Collections.reverse(path);
        if (path.isEmpty() || !path.get(0).equals(start)) {
            throw new NoLandRouteException("No land route found");
        }

        return path;
    }

    private String normalize(String code) {
        return code == null ? "" : code.toUpperCase(Locale.ROOT).trim();
    }
}

