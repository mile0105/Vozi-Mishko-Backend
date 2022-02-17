package com.vozimishko.backend.trip.service;

import com.vozimishko.backend.car.model.Car;
import com.vozimishko.backend.car.service.CarService;
import com.vozimishko.backend.cities.service.CityService;
import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.exceptions.NotFoundException;
import com.vozimishko.backend.error.exceptions.UnauthorizedException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.riderequest.service.RideRequestService;
import com.vozimishko.backend.security.PrincipalService;
import com.vozimishko.backend.trip.model.Trip;
import com.vozimishko.backend.trip.model.TripRequestBody;
import com.vozimishko.backend.trip.repository.TripRepository;
import com.vozimishko.backend.user.model.UserDetails;
import com.vozimishko.backend.user.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TripService {

  private final TripRepository tripRepository;
  private final TripMapper mapper;
  private final PrincipalService principalService;
  private final CarService carService;
  private final CityService cityService;
  private final UserService userService;
  private final RideRequestService rideRequestService;

  public Trip addTrip(TripRequestBody tripRequestBody) {
    Long loggedInUserId = principalService.getLoggedInUserId();

    validateTrip(tripRequestBody);
    Trip mappedTrip = mapper.mapToDbModelForAddition(tripRequestBody, loggedInUserId);

    return tripRepository.save(mappedTrip);
  }

  public Set<Trip> fetchTrips(Long startCityId, Long endCityId, LocalDate date) {

    Set<Trip> trips = null;

    if (startCityId != null && endCityId != null) {
      trips = tripRepository.getTripsByOriginAndDestination(startCityId, endCityId);
    }

    if (startCityId == null && endCityId == null) {
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

  private void validateTrip(TripRequestBody tripRequestBody) {

    //if we have a city, it's already valid
    cityService.findByIdOrThrow(tripRequestBody.getStartCityId());
    cityService.findByIdOrThrow(tripRequestBody.getEndCityId());

    if (Objects.equals(tripRequestBody.getEndCityId(), tripRequestBody.getStartCityId())) {
      throw new BadRequestException(ErrorMessage.TRIP_SAME_CITIES);
    }

    Set<Long> loggedInUserCarIds = carService.getLoggedInUserCars().stream().map(Car::getId).collect(Collectors.toSet());

    if (!loggedInUserCarIds.contains(tripRequestBody.getCarId())) {
      throw new BadRequestException(ErrorMessage.CAR_UNAVAILABLE);
    }
  }

  public void validateTripSeats(Trip trip) {
    Car car = carService.findByIdOrThrow(trip.getCarId());
    if (car.getNumberOfSeats() - 1 <= trip.getPassengerIds().size() + rideRequestService.getNumberOfUnconfirmedRideRequestsForTrip(trip.getId())) {
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
    if (!trip.getPassengerIds().contains(loggedInUser.intValue()) && !rideRequestService.passengerIsPartOfRideRequest(trip.getId(), loggedInUser)) {
      throw new BadRequestException(ErrorMessage.TRIP_DOES_NOT_CONTAIN_CUSTOMER);
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
