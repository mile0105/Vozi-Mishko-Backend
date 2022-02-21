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
import com.vozimishko.backend.trip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
    RideRequest rideRequest = processRideRequestForSubscription(rideRequestSubscriptionDto, driverId);

    rideRequestRepository.save(rideRequest);
  }

  private RideRequest processRideRequestForSubscription(RideRequestSubscriptionDto rideRequestSubscriptionDto, Long driverId) {

    Long tripId = rideRequestSubscriptionDto.getTripId();
    Long carId = rideRequestSubscriptionDto.getCarId();
    validateSubscription(tripId, carId);

    RideRequest rideRequest = findByIdOrThrow(rideRequestSubscriptionDto.getRideRequestId());
    if (!rideRequest.requestCanBeMade()) {
      throw new BadRequestException(ErrorMessage.RIDE_REQUEST_HAS_SUBSCRIPTION);
    }

    if (Objects.equals(driverId, rideRequest.getPassengerId())) {
      throw new BadRequestException(ErrorMessage.DRIVER_CANNOT_SUBSCRIBE);
    }

    if (tripId != null) {
      Trip existingTrip = tripService.findByIdOrThrow(tripId);
      validateExistingTrip(existingTrip, driverId);
    }

    if (carId != null) {
      validateCar(carId);
      Trip trip = tripService.addTrip(rideRequest.toTripRequestBody(carId));
      tripId = trip.getId();

    }

    //todo maybe add a configuration param for the hours
    return rideRequest.toBuilder().confirmationExpiry(LocalDateTime.now().plusHours(1)).tripId(tripId).build();
  }

  private RideRequest findByIdOrThrow(Long rideRequestId) {
    return rideRequestRepository.findById(rideRequestId).orElseThrow(() -> new NotFoundException(ErrorMessage.RIDE_REQUEST_NOT_FOUND));
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
