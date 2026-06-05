package com.jimmy.practice.routing.client;

import java.util.Map;
import java.util.Set;

public interface CountryGraphProvider {

    Map<String, Set<String>> getCountryBorderGraph();
}

