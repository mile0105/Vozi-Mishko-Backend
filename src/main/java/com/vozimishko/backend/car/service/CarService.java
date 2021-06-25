package com.vozimishko.backend.car.service;

import com.vozimishko.backend.car.model.Car;
import com.vozimishko.backend.car.model.CarApi;
import com.vozimishko.backend.car.repository.CarRepository;
import com.vozimishko.backend.error.exceptions.NotFoundException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.security.PrincipalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CarService {
  private final CarRepository carRepository;
  private final CarMapper carMapper;
  private final PrincipalService principalService;

  public Car addCar(CarApi carApi) {
    Long loggedInUserId = principalService.getLoggedInUserId();
    Car carDbModel = carMapper.transformToDbModel(carApi, loggedInUserId);
    return carRepository.save(carDbModel);
  }

  public List<Car> getLoggedInUserCars() {
    Long loggedInUserId = principalService.getLoggedInUserId();
    return carRepository.getCarsFromUser(loggedInUserId);
  }

  public Car findByIdOrThrow(Long carId) {
    return carRepository.findById(carId).orElseThrow(() -> new NotFoundException(ErrorMessage.CAR_NOT_FOUND));
  }
}
