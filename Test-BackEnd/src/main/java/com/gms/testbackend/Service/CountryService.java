package com.gms.testbackend.Service;

import com.gms.testbackend.Model.Country;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class CountryService {
    private List<Country> cache = new ArrayList<>();
    private long lastFetchTime = 0;

    private final long CACHE_DURATION = 10 * 60 * 1000;

    public List<Country> getCountries() {

        if (System.currentTimeMillis() - lastFetchTime > CACHE_DURATION) {
            fetchCountries();
        }

        return cache;
    }

    private void fetchCountries() {

        String url = "https://restcountries.com/v3.1/all";

        RestTemplate restTemplate = new RestTemplate();

        List<Map<String, Object>> response =
                restTemplate.getForObject(url, List.class);

        List<Country> countries = new ArrayList<>();

        for (Map<String, Object> item : response) {

            Country c = new Country();

            Map name = (Map) item.get("name");
            c.setName((String) name.get("common"));

            List capitalList = (List) item.get("capital");
            c.setCapital(capitalList != null ? capitalList.get(0).toString() : "N/A");

            c.setRegion((String) item.get("region"));

            Number pop = (Number) item.get("population");
            c.setPopulation(pop.longValue());

            Map flags = (Map) item.get("flags");
            c.setFlag((String) flags.get("png"));

            countries.add(c);
        }

        cache = countries;
        lastFetchTime = System.currentTimeMillis();
    }

    public List<Country> searchCountries(String keyword) {

        return getCountries()
                .stream()
                .filter(c -> c.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}
