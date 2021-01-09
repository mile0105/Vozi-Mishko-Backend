package com.vozimishko.backend.car.service;

import com.vozimishko.backend.car.model.Car;
import com.vozimishko.backend.car.model.CarApi;
import com.vozimishko.backend.security.PrincipalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class CarMapper {

  private final PrincipalService principalService;

  public Car transformToDbModel(CarApi carApi) {

    Long loggedInUserId = principalService.getLoggedInUserId();

    return Car.builder()
      .manufacturerName(carApi.getManufacturerName())
      .modelName(carApi.getModelName())
      .numberOfSeats(carApi.getNumberOfSeats())
      .userId(loggedInUserId)
      .build();
  }
}
