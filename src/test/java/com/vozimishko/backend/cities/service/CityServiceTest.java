package com.vozimishko.backend.cities.service;

import com.vozimishko.backend.cities.model.City;
import com.vozimishko.backend.cities.repository.CityRepository;
import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.model.ErrorMessage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CityServiceTest {

  @Mock
  private CityRepository cityRepository;

  private CityService cityService;

  @BeforeEach
  void setUp() {
    MockHttpServletRequest mockRequest = new MockHttpServletRequest();
    mockRequest.setParameter("lang", "en");
    RequestContextHolder.setRequestAttributes(new ServletRequestAttributes(mockRequest));

    cityService = new CityService(cityRepository);
  }

  @Test
  void shouldGetAllCities() {
    City city = new City();
    city.setEnglishName("Skopje");
    when(cityRepository.findAll()).thenReturn(new HashSet<>(Collections.singleton(city)));

    Set<City> result = cityService.getAllCities();

    assertEquals(Collections.singleton(city), result);
  }

  @Test
  void shouldFindCityById() {
    City city = new City();
    city.setId(1L);

    when(cityRepository.findById(1L)).thenReturn(Optional.of(city));

    City result = cityService.findById(1L);

    assertEquals(city, result);
  }

  @Test
  void shouldThrowExceptionIfACityCannotBeFound() {

    when(cityRepository.findById(any())).thenReturn(Optional.empty());

    BadRequestException badRequestException = assertThrows(BadRequestException.class, () -> cityService.findById(1L));

    assertEquals(ErrorMessage.CITY_NOT_FOUND, badRequestException.getErrorMessage());
  }
}
