package com.vozimishko.backend.car.service;

import com.vozimishko.backend.car.model.Car;
import com.vozimishko.backend.car.model.CarRequestBody;
import org.springframework.stereotype.Component;

@Component
public class CarMapper {

  public Car transformToDbModel(CarRequestBody carRequestBody, Long loggedInUserId) {

    return Car.builder()
      .manufacturerName(carRequestBody.getManufacturerName())
      .modelName(carRequestBody.getModelName())
      .numberOfSeats(carRequestBody.getNumberOfSeats())
      .userId(loggedInUserId)
      .build();
  }
}
