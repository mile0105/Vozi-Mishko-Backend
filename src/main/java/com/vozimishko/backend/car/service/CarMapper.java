package com.vozimishko.backend.car.service;

import com.vozimishko.backend.car.model.Car;
import com.vozimishko.backend.car.model.CarApi;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {

  public Car transformToDbModel(CarApi carApi, Long loggedInUserId) {

    return Car.builder()
      .manufacturerName(carApi.getManufacturerName())
      .modelName(carApi.getModelName())
      .numberOfSeats(carApi.getNumberOfSeats())
      .userId(loggedInUserId)
      .build();
  }
}
