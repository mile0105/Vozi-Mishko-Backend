package com.vozimishko.backend.car.repository;

import com.vozimishko.backend.car.model.Car;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CarRepository extends CrudRepository<Car, Long> {

  @Query("select * from cars where user_id = :userId")
  List<Car> getCarsFromUser(@Param("userId") Long userId);

}
