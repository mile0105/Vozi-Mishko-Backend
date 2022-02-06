package com.vozimishko.backend.riderequest.service;

import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.exceptions.NotFoundException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.riderequest.model.RideRequest;
import com.vozimishko.backend.riderequest.repository.RideRequestRepository;
import com.vozimishko.backend.trip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RideRequestSubscriptionConfirmationService {

  private final RideRequestRepository rideRequestRepository;
  private final TripService tripService;

  public void confirmSubscription(Long rideRequestId) {
    RideRequest rideRequest = findByIdOrThrow(rideRequestId);

    if (!rideRequest.canBeConfirmedOrDenied()) {
      throw new BadRequestException(ErrorMessage.RIDE_REQUEST_CANNOT_BE_CONFIRMED);
    }
    tripService.subscribeToTrip(rideRequest.getTripId());
    RideRequest confirmedRequest = rideRequest.toBuilder().isConfirmed(true).build();
    rideRequestRepository.save(confirmedRequest);
  }

  public void denySubscription(Long rideRequestId) {
    RideRequest rideRequest = findByIdOrThrow(rideRequestId);

    if (!rideRequest.canBeConfirmedOrDenied()) {
      throw new BadRequestException(ErrorMessage.RIDE_REQUEST_CANNOT_BE_DENIED);
    }

    RideRequest cancelledRequest = rideRequest.toBuilder().tripId(null).confirmationExpiry(null).build();
    rideRequestRepository.save(cancelledRequest);
  }

  private RideRequest findByIdOrThrow(Long rideRequestId) {
    return rideRequestRepository.findById(rideRequestId).orElseThrow(() -> new NotFoundException(ErrorMessage.RIDE_REQUEST_NOT_FOUND));
  }
}
