package com.vozimishko.backend.trip.service;

import com.vozimishko.backend.trip.model.Trip;
import com.vozimishko.backend.trip.model.TripApi;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Component
public class TripMapper {

  public Trip mapToDbModelForAddition(TripApi tripApi, Long driverId) {

    LocalDateTime localDateTime = LocalDateTime
      .parse(tripApi.getTimeOfDeparture(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    return Trip.builder()
      .start(tripApi.getStart().toUpperCase())
      .end(tripApi.getEnd().toUpperCase())
      .timeOfDeparture(localDateTime)
      .carId(tripApi.getCarId())
      .driverId(driverId)
      .passengerIds(new ArrayList<>())
      .build();
  }
}
