package com.vozimishko.backend.riderequest.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Data
public class RideRequestDto {

  @NotNull(message = "Please choose a starting city for your ride request")
  private Long startCityId;
  @NotNull(message = "Please choose a destination city for your ride request")
  private Long endCityId;
  @NotEmpty(message = "Please choose a date and time of departure")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private String timeOfDeparture;

  public RideRequest toDbModel(Long passengerId) {
    LocalDateTime departureTime = LocalDateTime
      .parse(timeOfDeparture, DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    return RideRequest.builder()
      .startCityId(startCityId)
      .endCityId(endCityId)
      .passengerId(passengerId)
      .timeOfDeparture(departureTime)
      .isConfirmed(false)
      .tripId(null)
      .build();
  }
}
