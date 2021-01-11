package com.vozimishko.backend.car.service;

import com.vozimishko.backend.car.model.Car;
import com.vozimishko.backend.car.model.CarApi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class CarMapperTest {

  private CarMapper carMapper;

  @BeforeEach
  void setUp() {
    carMapper = new CarMapper();
  }

  @Test
  void shouldTransformToDbModel() {
    Long loggedInUserId = 1L;
    CarApi carApi = CarApi.builder()
      .manufacturerName("Opel")
      .modelName("Vectra")
      .numberOfSeats(5)
      .build();

    Car result = carMapper.transformToDbModel(carApi, loggedInUserId);

    assertThat(result.getManufacturerName()).isEqualTo(carApi.getManufacturerName());
    assertThat(result.getModelName()).isEqualTo(carApi.getModelName());
    assertThat(result.getNumberOfSeats()).isEqualTo(carApi.getNumberOfSeats());
    assertThat(result.getUserId()).isEqualTo(loggedInUserId);

  }
}
