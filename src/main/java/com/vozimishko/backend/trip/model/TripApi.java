package com.vozimishko.backend.trip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripApi {

  @NotEmpty(message = "Please choose a starting city for your trip")
  private Long startCityId;
  @NotEmpty(message = "Please choose a destination city for your trip")
  private Long endCityId;
  @NotEmpty(message = "Please choose a date and time of departure")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private String timeOfDeparture;
  @NotEmpty(message = "Please choose a car for your trip")
  private Long carId;
}
