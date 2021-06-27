package com.vozimishko.backend.cities.repository;

import com.vozimishko.backend.cities.model.City;
import org.springframework.data.repository.CrudRepository;

public interface CityRepository extends CrudRepository<City, Long> {
}
