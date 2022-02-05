package com.vozimishko.backend.riderequest.service;

import com.vozimishko.backend.car.model.Car;
import com.vozimishko.backend.car.service.CarService;
import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.exceptions.NotFoundException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.riderequest.model.RideRequest;
import com.vozimishko.backend.riderequest.model.RideRequestSubscriptionDto;
import com.vozimishko.backend.riderequest.repository.RideRequestRepository;
import com.vozimishko.backend.security.PrincipalService;
import com.vozimishko.backend.trip.model.Trip;
import com.vozimishko.backend.trip.model.TripRequestBody;
import com.vozimishko.backend.trip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RideRequestSubscriptionService {

  private final PrincipalService principalService;
  private final RideRequestRepository rideRequestRepository;
  private final CarService carService;
  private final TripService tripService;

  public void driverSubscribe(RideRequestSubscriptionDto rideRequestSubscriptionDto) {

    Long driverId = principalService.getLoggedInUserId();
    RideRequest rideRequest = processTripForSubscription(rideRequestSubscriptionDto, driverId);

    rideRequestRepository.save(rideRequest);
  }

  public void confirmSubscription(Long rideRequestId) {

  }

  public void denySubscription(Long rideRequestId) {

  }

  private RideRequest processTripForSubscription(RideRequestSubscriptionDto rideRequestSubscriptionDto, Long driverId) {

    RideRequest rideRequest = findByIdOrThrow(rideRequestSubscriptionDto.getRideRequestId());
    if (rideRequest.getTripId() != null) {
      throw new BadRequestException(ErrorMessage.EMPTY);
    }

    Long tripId = rideRequestSubscriptionDto.getTripId();
    Long carId = rideRequestSubscriptionDto.getCarId();
    validateSubscription(tripId, carId);

    if (tripId != null) {
      Trip existingTrip = tripService.findByIdOrThrow(tripId);
      tripService.validateTripSeats(existingTrip);
      validateExistingTrip(existingTrip, driverId);
    }

    if (carId != null) {
      validateCar(carId);
      Trip trip = tripService.addTrip(rideRequest.toTripRequestBody(carId));
      tripId = trip.getId();

    }


    return rideRequest.toBuilder().tripId(tripId).build();
  }

  private RideRequest findByIdOrThrow(Long rideRequestId) {
    return rideRequestRepository.findById(rideRequestId).orElseThrow(() -> new NotFoundException(ErrorMessage.EMPTY));
  }

  private void validateSubscription(Long carId, Long tripId) {
    if (carId == null && tripId == null) {
      throw new BadRequestException(ErrorMessage.RIDE_REQUEST_SUBSCRIPTION_CAR_AND_TRIP_NULL);
    }
  }

  private void validateExistingTrip(Trip trip, Long driverId) {
    if (!Objects.equals(driverId, trip.getDriverId())) {
      throw new BadRequestException(ErrorMessage.YOU_ARE_NOT_THE_DRIVER);
    }
    tripService.validateTripSeats(trip);
  }

  private void validateCar(Long carId) {

    Set<Long> loggedInUserCarIds = carService.getLoggedInUserCars().stream().map(Car::getId).collect(Collectors.toSet());
    if (!loggedInUserCarIds.contains(carId)) {
      throw new BadRequestException(ErrorMessage.CAR_UNAVAILABLE);
    }

  }

}
