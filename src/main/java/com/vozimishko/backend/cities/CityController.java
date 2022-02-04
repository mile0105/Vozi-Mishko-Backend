package com.vozimishko.backend.cities;

import com.vozimishko.backend.cities.model.City;
import com.vozimishko.backend.cities.service.CityService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;

@RestController
@RequestMapping("/api/v1/cities")
@RequiredArgsConstructor
public class CityController {

  private final CityService cityService;

  @GetMapping("")
  public ResponseEntity<Set<City>> getAllCities() {
    return ResponseEntity.ok(cityService.getAllCities());
  }

  @GetMapping("/{id}")
  public ResponseEntity<City> getCityById(@PathVariable(name = "id") Long id) {
    return ResponseEntity.ok(cityService.findById(id));
  }
}
