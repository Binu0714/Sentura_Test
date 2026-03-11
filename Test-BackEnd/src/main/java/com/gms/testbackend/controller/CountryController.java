package com.gms.testbackend.controller;

import com.gms.testbackend.Model.Country;
import com.gms.testbackend.Service.CountryService;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/countries")
@CrossOrigin
public class CountryController {
    private final CountryService service;

    public CountryController(CountryService service) {
        this.service = service;
    }

    @GetMapping
    public List<Country> getCountries() {
        return service.getCountries();
    }

    @GetMapping("/search")
    public List<Country> searchCountries(@RequestParam String name) {
        return service.searchCountries(name);
    }
}
