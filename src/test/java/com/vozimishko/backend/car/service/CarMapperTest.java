package com.vozimishko.backend.car.service;

import com.vozimishko.backend.car.model.Car;
import com.vozimishko.backend.car.model.CarRequestBody;
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
    CarRequestBody carRequestBody = CarRequestBody.builder()
      .manufacturerName("Opel")
      .modelName("Vectra")
      .numberOfSeats(5)
      .build();

    Car result = carMapper.transformToDbModel(carRequestBody, loggedInUserId);

    assertThat(result.getManufacturerName()).isEqualTo(carRequestBody.getManufacturerName());
    assertThat(result.getModelName()).isEqualTo(carRequestBody.getModelName());
    assertThat(result.getNumberOfSeats()).isEqualTo(carRequestBody.getNumberOfSeats());
    assertThat(result.getUserId()).isEqualTo(loggedInUserId);

  }
}
