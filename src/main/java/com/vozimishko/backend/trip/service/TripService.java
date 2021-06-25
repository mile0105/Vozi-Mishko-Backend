package com.vozimishko.backend.trip.service;

import com.vozimishko.backend.car.model.Car;
import com.vozimishko.backend.car.service.CarService;
import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.exceptions.NotFoundException;
import com.vozimishko.backend.error.exceptions.UnauthorizedException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.security.PrincipalService;
import com.vozimishko.backend.trip.model.Trip;
import com.vozimishko.backend.trip.model.TripApi;
import com.vozimishko.backend.trip.repository.TripRepository;
import com.vozimishko.backend.user.model.UserDetails;
import com.vozimishko.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
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

  public Set<Trip> fetchTrips(String start, String end, LocalDate date) {

    Set<Trip> trips = null;

    if (StringUtils.hasText(start) && StringUtils.hasText(end)) {
      trips = tripRepository.getTripsByOriginAndDestination(start.toUpperCase(), end.toUpperCase());
    }

    if(!StringUtils.hasText(start) && !StringUtils.hasText(end)) {
      trips = StreamSupport.stream(tripRepository.findAll().spliterator(), false).collect(Collectors.toSet());
    }

    if (trips == null) {
      throw new BadRequestException(ErrorMessage.EMPTY);
    }

    return filterTripsByDateAndSort(trips, date);
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
    return tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException(ErrorMessage.TRIP_NOT_FOUND));
  }

  public Trip unsubscribeFromTrip(Long tripId) {

    Long loggedInUserId = principalService.getLoggedInUserId();
    Trip trip = findByIdOrThrow(tripId);
    validateDriverDoesntExist(trip, loggedInUserId);
    validateCustomerIdExists(trip, loggedInUserId);

    List<Integer> updatedPassengerIds = new ArrayList<>(trip.getPassengerIds());
    updatedPassengerIds.remove((Integer) loggedInUserId.intValue());
    Trip newTrip = trip.toBuilder().passengerIds(updatedPassengerIds).build();

    return tripRepository.save(newTrip);
  }

  public Set<Trip> getTripsWhereIDrive() {
    Long loggedInUserId = principalService.getLoggedInUserId();

    return new TreeSet<>(tripRepository.getTripsByDriverId(loggedInUserId));
  }

  public Set<Trip> getTripsWhereIAmSubscribed() {
    Long loggedInUserId = principalService.getLoggedInUserId();

    return new TreeSet<>(tripRepository.getTripsByPassengerId(loggedInUserId));
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

    if (userDetails.size() != 1) {
      throw new BadRequestException(ErrorMessage.SOMETHING_WENT_WRONG);
    }

    return userDetails.get(0);
  }


  private void validateTripSeats(Trip trip) {
    Car car = carService.findByIdOrThrow(trip.getCarId());
    if (car.getNumberOfSeats() - 1 <= trip.getPassengerIds().size()) {
      throw new BadRequestException(ErrorMessage.TRIP_IS_FULL);
    }
  }

  private void validateDriverDoesntExist(Trip trip, Long loggedInUser) {
    if (trip.getDriverId().equals(loggedInUser)) {
      throw new BadRequestException(ErrorMessage.DRIVER_CANNOT_SUBSCRIBE);
    }
  }

  private void validateCustomerIdDoesntExist(Trip trip, Long loggedInUser) {
    if (trip.getPassengerIds().contains(loggedInUser.intValue())) {
      throw new BadRequestException(ErrorMessage.TRIP_ALREADY_CONTAINS_CUSTOMER);
    }
  }

  private void validateCustomerIdExists(Trip trip, Long loggedInUser) {
    if (!trip.getPassengerIds().contains(loggedInUser.intValue())) {
      throw new BadRequestException(ErrorMessage.TRIP_DOES_NOT_CONTAINS_CUSTOMER);
    }
  }

  private void validateUserIsDriver(Trip trip, Long loggedInUser) {
    if (!trip.getDriverId().equals(loggedInUser)) {
      throw new UnauthorizedException(ErrorMessage.NO_PERMISSIONS);
    }
  }

  private Set<Trip> filterTripsByDateAndSort(Set<Trip> trips, LocalDate date) {
    if (date != null) {
      return trips.stream().filter(trip -> trip.getTimeOfDeparture().toLocalDate().equals(date)).collect(Collectors.toCollection(TreeSet::new));
    }

    return new TreeSet<>(trips);
  }

}