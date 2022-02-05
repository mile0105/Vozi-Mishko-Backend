package com.vozimishko.backend.riderequest.model;


import com.vozimishko.backend.trip.model.TripRequestBody;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

@Getter
@Builder(toBuilder = true)
@Table("ride_requests")
@EqualsAndHashCode
public class RideRequest implements Comparable<RideRequest> {

  @Id
  private Long id;
  private Long startCityId;
  private Long endCityId;
  private LocalDateTime timeOfDeparture;
  private Long passengerId;
  private Long tripId;
  private boolean isConfirmed;

  @Override
  public int compareTo(RideRequest other) {
    return other.getTimeOfDeparture().isAfter(timeOfDeparture)? 1: -1;
  }

  public TripRequestBody toTripRequestBody(Long carId) {
    return TripRequestBody.builder()
      .startCityId(startCityId)
      .endCityId(endCityId)
      .timeOfDeparture(timeOfDeparture.toString())
      .carId(carId)
      .build();
  }
}
