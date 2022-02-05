package com.vozimishko.backend.trip.repository;

import com.vozimishko.backend.trip.model.Trip;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface TripRepository extends CrudRepository<Trip, Long> {

  @Query("select * from trips where start_city_id = :start_city_id and end_city_id = :end_city_id")
  Set<Trip> getTripsByOriginAndDestination(@Param("start_city_id") Long startCityId, @Param("end_city_id") Long endCityId);

  @Query("select * from trips where driver_id = :driverId")
  Set<Trip> getTripsByDriverId(@Param("driverId") Long driverId);

  @Query("select * from trips where :passengerId = ANY(passenger_ids)")
  Set<Trip> getTripsByPassengerId(@Param("passengerId") Long passengerId);
}
