package com.vozimishko.backend.riderequest.model;

import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import javax.validation.constraints.NotEmpty;

@Data
public class RideRequestDto {

  @NotEmpty(message = "Please choose a starting city for your ride request")
  private Long startCityId;
  @NotEmpty(message = "Please choose a destination city for your ride request")
  private Long endCityId;
  @NotEmpty(message = "Please choose a date and time of departure")
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private String timeOfDeparture;
}
