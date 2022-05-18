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
public class TripRequestBody {

  @NotEmpty(message = "Please choose a starting city for your trip")
  private Long startCityId;
  @NotEmpty(message = "Please choose a destination city for your trip")
  private Long endCityId;
  @NotEmpty(message = "Please choose a date and time of departure")
  @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss")
  private String timeOfDeparture;
  @NotEmpty(message = "Please choose a car for your trip")
  private Long carId;
  @NotEmpty(message = "Please enter a price for your trip")
  private Double tripPrice;
  private Double documentPrice;
  private Integer maximumNumberOfDocuments;

}
