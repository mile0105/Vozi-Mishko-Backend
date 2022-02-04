package com.vozimishko.backend.trip.model;

import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Builder(toBuilder = true)
@Table("trips")
@EqualsAndHashCode
public class Trip implements Comparable<Trip> {

  @Id
  private Long id;
  private Long startCityId;
  private Long endCityId;
  @Column("time_of_departure")
  private LocalDateTime timeOfDeparture;
  @Column("driver_id")
  private Long driverId;
  @Column("car_id")
  private Long carId;
  @Column("passenger_ids")
  private List<Integer> passengerIds;

  @Override
  public int compareTo(Trip other) {
    return other.getTimeOfDeparture().isAfter(timeOfDeparture)? 1: -1;
  }
}
