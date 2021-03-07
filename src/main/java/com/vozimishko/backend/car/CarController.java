package com.vozimishko.backend.car;

import com.vozimishko.backend.car.model.Car;
import com.vozimishko.backend.car.model.CarApi;
import com.vozimishko.backend.car.service.CarService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("api/v1/cars")
@RequiredArgsConstructor
public class CarController {

  private final CarService carService;

  @PostMapping("")
  public ResponseEntity<Car> addCar(@RequestBody CarApi carApi) {
    Car resultCar = carService.addCar(carApi);
    return ResponseEntity.ok(resultCar);
  }

  @GetMapping("/me")
  public ResponseEntity<List<Car>> getMyCars() {
    List<Car> cars = carService.getLoggedInUserCars();
    return ResponseEntity.ok(cars);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Car> getCarById(@PathVariable(name = "id") Long carId) {
    Car car = carService.findByIdOrThrow(carId);
    return ResponseEntity.ok(car);
  }

}
