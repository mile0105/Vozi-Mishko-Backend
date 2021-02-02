package com.vozimishko.backend.trip.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TripApi {
  private String start;
  private String end;
  @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  private String timeOfDeparture;
  private Long carId;
}
