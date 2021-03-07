package com.vozimishko.backend.trip.service;

import com.vozimishko.backend.car.model.Car;
import com.vozimishko.backend.car.service.CarService;
import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.exceptions.NotFoundException;
import com.vozimishko.backend.error.exceptions.UnauthorizedException;
import com.vozimishko.backend.security.PrincipalService;
import com.vozimishko.backend.trip.model.Trip;
import com.vozimishko.backend.trip.model.TripApi;
import com.vozimishko.backend.trip.repository.TripRepository;
import com.vozimishko.backend.user.model.UserDetails;
import com.vozimishko.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TripService {

  private final TripRepository tripRepository;
  private final TripMapper mapper;
  private final PrincipalService principalService;
  private final CarService carService;
  private final UserService userService;

  public Trip addTrip(TripApi tripApi) {
    Long loggedInUserId = principalService.getLoggedInUserId();

    Trip mappedTrip = mapper.mapToDbModelForAddition(tripApi, loggedInUserId);

    return tripRepository.save(mappedTrip);
  }

  public List<Trip> fetchTrips(String start, String end) {

    if (StringUtils.hasText(start) && StringUtils.hasText(end)) {
      return tripRepository.getTripsByOriginAndDestination(start.toUpperCase(), end.toUpperCase());
    }

    if(!StringUtils.hasText(start) && !StringUtils.hasText(end)) {
      return StreamSupport.stream(tripRepository.findAll().spliterator(), false)
        .collect(Collectors.toList());
    }

    throw new BadRequestException("");
  }

  public Trip subscribeToTrip(Long tripId) {

    Long loggedInUserId = principalService.getLoggedInUserId();
    Trip trip = findByIdOrThrow(tripId);
    validateDriverDoesntExist(trip, loggedInUserId);
    validateCustomerIdDoesntExist(trip, loggedInUserId);
    validateTripSeats(trip);

    List<Integer> updatedPassengerIds = new ArrayList<>(trip.getPassengerIds());
    updatedPassengerIds.add(loggedInUserId.intValue());
    Trip newTrip = trip.toBuilder().passengerIds(updatedPassengerIds).build();

    return tripRepository.save(newTrip);
  }

  public Trip findByIdOrThrow(Long tripId) {
    return tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException("Trip not found"));
  }

  public Trip unsubscribeFromTrip(Long tripId) {

    Long loggedInUserId = principalService.getLoggedInUserId();
    Trip trip = findByIdOrThrow(tripId);
    validateDriverDoesntExist(trip, loggedInUserId);
    validateCustomerIdExists(trip, loggedInUserId);

    List<Integer> updatedPassengerIds = new ArrayList<>(trip.getPassengerIds());
    updatedPassengerIds.remove(loggedInUserId.intValue());
    Trip newTrip = trip.toBuilder().passengerIds(updatedPassengerIds).build();

    return tripRepository.save(newTrip);
  }

  public List<Trip> getTripsWhereIDrive() {
    Long loggedInUserId = principalService.getLoggedInUserId();

    return tripRepository.getTripsByDriverId(loggedInUserId);
  }

  public List<Trip> getTripsWhereIAmSubscribed() {
    Long loggedInUserId = principalService.getLoggedInUserId();

    return tripRepository.getTripsByPassengerId(loggedInUserId);
  }

  public List<UserDetails> getMyPassengerDetails(Long tripId) {
    Long loggedInUserId = principalService.getLoggedInUserId();
    Trip trip = findByIdOrThrow(tripId);
    validateUserIsDriver(trip, loggedInUserId);

    return userService.getUserDetails(trip.getPassengerIds().stream().map(Integer::longValue).collect(Collectors.toList()));
  }

  public UserDetails getDriverDetails(Long tripId) {
    Long loggedInUserId = principalService.getLoggedInUserId();
    Trip trip = findByIdOrThrow(tripId);
    validateCustomerIdExists(trip, loggedInUserId);

    List<UserDetails> userDetails = userService.getUserDetails(Collections.singletonList(trip.getDriverId()));

    if(userDetails.size() != 1) {
      throw new BadRequestException("Something went wrong, if this problem persists, please contact us");
    }

    return userDetails.get(0);
  }


  private void validateTripSeats(Trip trip) {
    Car car = carService.findByIdOrThrow(trip.getCarId());
    if (car.getNumberOfSeats() - 1 <= trip.getPassengerIds().size()) {
      throw new BadRequestException("Trip is full, please select another trip");
    }
  }

  private void validateDriverDoesntExist(Trip trip, Long loggedInUser) {
    if (trip.getDriverId().equals(loggedInUser)) {
      throw new BadRequestException("Driver cannot further subscribe/unsubscribe from trip");
    }
  }

  private void validateCustomerIdDoesntExist(Trip trip, Long loggedInUser) {
    if (trip.getPassengerIds().contains(loggedInUser)) {
      throw new BadRequestException("Trip already contains customer");
    }
  }

  private void validateCustomerIdExists(Trip trip, Long loggedInUser) {
    if (!trip.getPassengerIds().contains(loggedInUser)) {
      throw new BadRequestException("Trip does not contain customer");
    }
  }

  private void validateUserIsDriver(Trip trip, Long loggedInUser) {
    if (!trip.getDriverId().equals(loggedInUser)) {
      throw new UnauthorizedException("You do not have permissions to view this");
    }
  }

}
