package com.vozimishko.backend.trip.repository;

import com.vozimishko.backend.trip.model.Trip;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TripRepository extends CrudRepository<Trip, Long> {

  @Query("select * from trips where start = :start and \"END\" = :end")
  public List<Trip> getTripsByOriginAndDestination(@Param("start") String start, @Param("end") String end);

  @Query("select * from trips where driverId = :driverId")
  public List<Trip> getTripsByDriverId(@Param("driverId") Long driverId);
}
