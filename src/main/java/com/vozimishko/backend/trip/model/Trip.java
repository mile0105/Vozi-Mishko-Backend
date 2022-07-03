package com.vozimishko.backend.trip.model;

import com.fasterxml.jackson.annotation.JsonGetter;
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
  @Column("trip_price")
  private Double tripPrice;
  @Column("document_price")
  private Double documentPrice;
  @Column("maximum_number_of_documents")
  private Integer maximumNumberOfDocuments;
  @Column("maximum_capacity")
  private Integer maximumCapacity;
  @Column("number_of_documents")
  private Integer numberOfDocuments;

  @Override
  public int compareTo(Trip other) {
    return other.getTimeOfDeparture().isAfter(timeOfDeparture)? 1: -1;
  }

  @JsonGetter(value = "canCarryDocuments")
  public Boolean canCarryDocuments() {
    return numberOfDocuments < maximumNumberOfDocuments;
  }
}
