package com.vozimishko.backend.riderequest.model;

import lombok.Data;

import javax.validation.constraints.NotEmpty;

@Data
public class RideRequestSubscriptionDto {

  private Long carId;

  @NotEmpty(message = "Please enter the ride request id")
  private Long rideRequestId;
  private Long tripId;
}
