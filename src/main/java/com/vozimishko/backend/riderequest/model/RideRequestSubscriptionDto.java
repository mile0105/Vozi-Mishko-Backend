package com.vozimishko.backend.riderequest.model;

import lombok.Data;

import javax.validation.constraints.NotNull;

@Data
public class RideRequestSubscriptionDto {

  private Long carId;

  @NotNull(message = "Please enter the ride request id")
  private Long rideRequestId;
  private Long tripId;
}
