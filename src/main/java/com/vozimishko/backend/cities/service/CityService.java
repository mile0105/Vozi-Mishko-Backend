package com.vozimishko.backend.cities.service;

import com.vozimishko.backend.cities.model.City;
import com.vozimishko.backend.cities.repository.CityRepository;
import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.model.ErrorMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class CityService {

  private final CityRepository cityRepository;

  public Set<City> getAllCities() {
    Iterable<City> cityIterable = cityRepository.findAll();
    return StreamSupport.stream(cityIterable.spliterator(), false).collect(Collectors.toCollection(TreeSet::new));
  }

  public City findById(Long id) {
    return cityRepository.findById(id).orElseThrow(() -> new BadRequestException(ErrorMessage.CITY_NOT_FOUND));
  }
}
