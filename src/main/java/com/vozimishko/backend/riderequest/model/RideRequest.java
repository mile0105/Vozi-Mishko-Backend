package com.vozimishko.backend.riderequest.model;


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
public class RideRequest {

  @Id
  private Long id;
  private Long startCityId;
  private Long endCityId;
  private LocalDateTime timeOfDeparture;
  private Long passengerId;
  private Long tripId;
  private boolean isConfirmed;
}
