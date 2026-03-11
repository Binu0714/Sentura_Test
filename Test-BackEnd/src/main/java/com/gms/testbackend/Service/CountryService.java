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
    private final long CACHE_DURATION = 10 * 60 * 1000; // 10 minutes

    public List<Country> getCountries() {
        if (System.currentTimeMillis() - lastFetchTime > CACHE_DURATION) {
            fetchCountries();
        }
        return cache;
    }

    private void fetchCountries() {
        String url = "https://restcountries.com/v3.1/all?fields=name,capital,region,population,flags";
        RestTemplate restTemplate = new RestTemplate();

        List<Map<String, Object>> response;
        try {
            response = restTemplate.getForObject(url, List.class);
        } catch (Exception e) {
            System.out.println("Error fetching countries: " + e.getMessage());
            return;
        }

        List<Country> countries = new ArrayList<>();

        for (Map<String, Object> item : response) {
            try {
                Country c = new Country();

                Map nameMap = (Map) item.get("name");
                c.setName(nameMap != null ? nameMap.get("common").toString() : "N/A");

                List capitalList = (List) item.get("capital");
                c.setCapital(capitalList != null && !capitalList.isEmpty() ? capitalList.get(0).toString() : "N/A");

                c.setRegion(item.get("region") != null ? item.get("region").toString() : "N/A");

                Number pop = (Number) item.get("population");
                c.setPopulation(pop != null ? pop.longValue() : 0);

                Map flagsMap = (Map) item.get("flags");
                c.setFlag(flagsMap != null && flagsMap.get("png") != null ? flagsMap.get("png").toString() : "");

                countries.add(c);

            } catch (Exception ex) {
                System.out.println("Skipping country due to error: " + ex.getMessage());
            }
        }

        cache = countries;
        lastFetchTime = System.currentTimeMillis();
    }

    public List<Country> searchCountries(String keyword) {
        return getCountries().stream()
                .filter(c -> c.getName().toLowerCase().contains(keyword.toLowerCase()))
                .collect(Collectors.toList());
    }
}