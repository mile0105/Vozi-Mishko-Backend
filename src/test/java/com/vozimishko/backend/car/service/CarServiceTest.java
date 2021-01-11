package com.vozimishko.backend.car.service;


import com.vozimishko.backend.car.model.Car;
import com.vozimishko.backend.car.model.CarApi;
import com.vozimishko.backend.car.repository.CarRepository;
import com.vozimishko.backend.security.PrincipalService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CarServiceTest {
  @Mock
  private CarRepository carRepository;
  @Mock
  private CarMapper carMapper;
  @Mock
  private PrincipalService principalService;
  private CarService carService;

  private Car testCar;
  private CarApi testCarApi;
  private Long loggedInUserId;

  @BeforeEach
  void setUp() {
    loggedInUserId = 1L;
    testCarApi = CarApi.builder().manufacturerName("Opel").modelName("Vectra").numberOfSeats(5).build();
    testCar = Car.builder().manufacturerName("Opel").modelName("Vectra").numberOfSeats(5).userId(loggedInUserId).build();
    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);

    carService = new CarService(carRepository, carMapper, principalService);
  }

  @Test
  void shouldAddCarToRepository() {
    when(carRepository.save(testCar)).thenReturn(testCar);
    when(carMapper.transformToDbModel(testCarApi, loggedInUserId)).thenReturn(testCar);

    Car result = carService.addCar(testCarApi);
    assertThat(result).isEqualTo(testCar);
  }

  @Test
  void shouldRetrieveTheCarsOfTheLoggedInUser() {
    List<Car> userCars = Collections.singletonList(testCar);
    when(carRepository.getCarsFromUser(loggedInUserId)).thenReturn(userCars);

    List<Car> loggedInUserCars = carService.getLoggedInUserCars();

    assertThat(loggedInUserCars).isEqualTo(userCars);
  }
}
