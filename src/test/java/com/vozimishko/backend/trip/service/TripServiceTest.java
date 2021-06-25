package com.vozimishko.backend.trip.service;

import com.vozimishko.backend.car.model.Car;
import com.vozimishko.backend.car.service.CarService;
import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.exceptions.UnauthorizedException;
import com.vozimishko.backend.security.PrincipalService;
import com.vozimishko.backend.trip.model.Trip;
import com.vozimishko.backend.trip.model.TripApi;
import com.vozimishko.backend.trip.repository.TripRepository;
import com.vozimishko.backend.user.model.UserDetails;
import com.vozimishko.backend.user.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class TripServiceTest {

  @Mock
  private TripRepository tripRepository;
  @Mock
  private TripMapper mapper;
  @Mock
  private PrincipalService principalService;
  @Mock
  private CarService carService;
  @Mock
  private UserService userService;

  private TripService tripService;

  private LocalDateTime earlierTripTime;
  private LocalDateTime middleTripTime;
  private LocalDateTime laterTripTime;

  @BeforeEach
  void setUp() {
    earlierTripTime = LocalDateTime.of(2021, 1, 1, 0, 0);
    middleTripTime = LocalDateTime.of(2021, 1, 1, 1, 0);
    laterTripTime = LocalDateTime.of(2021, 1, 2, 0, 0);
    tripService = new TripService(tripRepository, mapper, principalService, carService, userService);
  }

  @Test
  void shouldTestAddingTrip() {
    Long loggedInUserId = 1L;
    TripApi tripApi = TripApi.builder().build();
    Trip trip = Trip.builder().build();
    Trip savedTrip = Trip.builder().driverId(loggedInUserId).build();
    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);
    when(mapper.mapToDbModelForAddition(tripApi, loggedInUserId)).thenReturn(trip);
    when(tripRepository.save(trip)).thenReturn(savedTrip);

    Trip result = tripService.addTrip(tripApi);

    assertThat(result).isEqualTo(savedTrip);
  }

  @Test
  void shouldTestFetchingTripsWhenDefiningStartAndEnd() {
    String start = "start";
    String end = "end";

    Trip earliestTrip = Trip.builder().timeOfDeparture(earlierTripTime).build();
    Trip middleTrip = Trip.builder().timeOfDeparture(middleTripTime).build();
    Trip latestTrip = Trip.builder().timeOfDeparture(laterTripTime).build();
    Set<Trip> trips = new HashSet<>(Arrays.asList(latestTrip, earliestTrip, middleTrip));

    when(tripRepository.getTripsByOriginAndDestination("START", "END")).thenReturn(trips);

    Set<Trip> result = tripService.fetchTrips(start, end, null);

    List<Trip> tripsCopy = new ArrayList<>(result);
    assertThat(result.size()).isEqualTo(3);
    assertThat(tripsCopy.get(0)).isEqualTo(latestTrip);
    assertThat(tripsCopy.get(1)).isEqualTo(middleTrip);
    assertThat(tripsCopy.get(2)).isEqualTo(earliestTrip);
  }

  @Test
  void shouldTestFilteringTripsOnGivenDateWhenDefiningStartAndEnd() {
    String start = "start";
    String end = "end";

    Trip earliestTrip = Trip.builder().timeOfDeparture(earlierTripTime).build();
    Trip middleTrip = Trip.builder().timeOfDeparture(middleTripTime).build();
    Trip latestTrip = Trip.builder().timeOfDeparture(laterTripTime).build();
    Set<Trip> trips = new HashSet<>(Arrays.asList(latestTrip, earliestTrip, middleTrip));

    when(tripRepository.getTripsByOriginAndDestination("START", "END")).thenReturn(trips);

    Set<Trip> result = tripService.fetchTrips(start, end, LocalDate.of(2021, 1, 1));

    List<Trip> tripsCopy = new ArrayList<>(result);
    assertThat(result.size()).isEqualTo(2);
    assertThat(tripsCopy.get(0)).isEqualTo(middleTrip);
    assertThat(tripsCopy.get(1)).isEqualTo(earliestTrip);
  }

  @Test
  void shouldTestFetchingAllTripsOnGivenDateWhen() {
    Trip earliestTrip = Trip.builder().timeOfDeparture(earlierTripTime).build();
    Trip middleTrip = Trip.builder().timeOfDeparture(middleTripTime).build();
    Trip latestTrip = Trip.builder().timeOfDeparture(laterTripTime).build();
    Set<Trip> trips = new HashSet<>(Arrays.asList(latestTrip, earliestTrip, middleTrip));

    when(tripRepository.findAll()).thenReturn(new HashSet<>(trips));

    Set<Trip> result = tripService.fetchTrips(null, null, LocalDate.of(2021, 1, 1));

    List<Trip> tripsCopy = new ArrayList<>(result);
    assertThat(result.size()).isEqualTo(2);
    assertThat(tripsCopy.get(0)).isEqualTo(middleTrip);
    assertThat(tripsCopy.get(1)).isEqualTo(earliestTrip);
  }

  @Test
  void shouldTestFetchingAllTrips() {

    Trip earliestTrip = Trip.builder().timeOfDeparture(earlierTripTime).build();
    Trip middleTrip = Trip.builder().timeOfDeparture(middleTripTime).build();
    Trip latestTrip = Trip.builder().timeOfDeparture(laterTripTime).build();
    Set<Trip> trips = new HashSet<>(Arrays.asList(latestTrip, earliestTrip, middleTrip));

    when(tripRepository.findAll()).thenReturn(new HashSet<>(trips));

    Set<Trip> result = tripService.fetchTrips(null, null, null);

    List<Trip> tripsCopy = new ArrayList<>(result);
    assertThat(result.size()).isEqualTo(3);
    assertThat(tripsCopy.get(0)).isEqualTo(latestTrip);
    assertThat(tripsCopy.get(1)).isEqualTo(middleTrip);
    assertThat(tripsCopy.get(2)).isEqualTo(earliestTrip);
  }

  @Test
  void shouldSubscribeToTrip() {
    Long tripId = 2L;
    long loggedInUserId = 1L;
    Long carId = 3L;

    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);
    Trip trip = Trip.builder().driverId(loggedInUserId + 1).carId(carId).passengerIds(new ArrayList<>()).build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

    Car testCar = Car.builder().numberOfSeats(5).build();
    when(carService.findByIdOrThrow(carId)).thenReturn(testCar);

    Trip tripResult = trip.toBuilder().passengerIds(Collections.singletonList((int) loggedInUserId)).build();

    tripService.subscribeToTrip(tripId);

    verify(tripRepository).save(tripResult);
  }

  @Test
  void shouldThrowExceptionIfUserIsDriverWhenSubscribing() {
    Long tripId = 2L;
    Long loggedInUserId = 1L;
    Long carId = 3L;

    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);
    Trip trip = Trip.builder().driverId(loggedInUserId).carId(carId).passengerIds(new ArrayList<>()).build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

    assertThrows(BadRequestException.class, () -> {
      tripService.subscribeToTrip(tripId);
    }, "Driver cannot further subscribe/unsubscribe from trip");

    verify(carService, never()).findByIdOrThrow(carId);
    verify(tripRepository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfUserIsContainedWithinPassengersWhenSubscribing() {
    Long tripId = 2L;
    Long loggedInUserId = 1L;
    Long carId = 3L;

    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);
    Trip trip = Trip.builder().driverId(loggedInUserId + 1).carId(carId)
      .passengerIds(Collections.singletonList(loggedInUserId.intValue())).build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

    assertThrows(BadRequestException.class, () -> {
      tripService.subscribeToTrip(tripId);
    }, "Trip already contains customer");

    verify(carService, never()).findByIdOrThrow(carId);
    verify(tripRepository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfCarIsFullWhenSubscribing() {
    Long tripId = 2L;
    Long loggedInUserId = 1L;
    Long carId = 3L;

    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);
    Trip trip = Trip.builder().driverId(loggedInUserId + 1).carId(carId)
      .passengerIds(Arrays.asList(10, 11, 12, 13)).build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

    Car testCar = Car.builder().numberOfSeats(5).build();
    when(carService.findByIdOrThrow(carId)).thenReturn(testCar);

    assertThrows(BadRequestException.class, () -> {
      tripService.subscribeToTrip(tripId);
    }, "Trip is full, please select another trip");

    verify(tripRepository, never()).save(any());
  }

  @Test
  void shouldUnSubscribeFromTrip() {
    Long tripId = 2L;
    Long loggedInUserId = 1L;
    Long carId = 3L;

    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);
    Trip trip = Trip.builder().driverId(loggedInUserId + 1).carId(carId)
      .passengerIds(Collections.singletonList(loggedInUserId.intValue())).build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

    Trip tripResult = trip.toBuilder().passengerIds(new ArrayList<>()).build();

    tripService.unsubscribeFromTrip(tripId);

    verify(tripRepository).save(tripResult);
  }

  @Test
  void shouldThrowExceptionIfUserIsDriverWhenUnSubscribing() {
    Long tripId = 2L;
    Long loggedInUserId = 1L;
    Long carId = 3L;

    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);
    Trip trip = Trip.builder().driverId(loggedInUserId).carId(carId).passengerIds(new ArrayList<>()).build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

    assertThrows(BadRequestException.class, () -> {
      tripService.unsubscribeFromTrip(tripId);
    }, "Driver cannot further subscribe/unsubscribe from trip");

    verify(tripRepository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfUserIsNotContainedWithinPassengersWhenUnsubscribing() {
    Long tripId = 2L;
    Long loggedInUserId = 1L;
    Long carId = 3L;

    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);
    Trip trip = Trip.builder().driverId(loggedInUserId + 1).carId(carId)
      .passengerIds(new ArrayList<>()).build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

    assertThrows(BadRequestException.class, () -> {
      tripService.unsubscribeFromTrip(tripId);
    }, "Trip does not contain customer");

    verify(tripRepository, never()).save(any());
  }

  @Test
  void shouldGetTripsWhereIAmDriver() {
    long loggedInUserId = 1L;
    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);
    Trip trip = Trip.builder().timeOfDeparture(laterTripTime).carId(loggedInUserId + 1).driverId(loggedInUserId).build();
    when(tripRepository.getTripsByDriverId(loggedInUserId)).thenReturn(Collections.singleton(trip));

    Set<Trip> result = tripService.getTripsWhereIDrive();
    List<Trip> tripCopy = new ArrayList<>(result);

    assertThat(tripCopy.size()).isEqualTo(1);
    assertThat(tripCopy.get(0)).isEqualTo(trip);
  }


  @Test
  void shouldGetTripsWhereIAmPassenger() {
    long loggedInUserId = 1L;
    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);
    Trip trip = Trip.builder().timeOfDeparture(laterTripTime).carId(loggedInUserId + 1).driverId(loggedInUserId - 1).build();

    when(tripRepository.getTripsByPassengerId(loggedInUserId)).thenReturn(Collections.singleton(trip));

    Set<Trip> result = tripService.getTripsWhereIAmSubscribed();
    List<Trip> tripCopy = new ArrayList<>(result);

    assertThat(tripCopy.size()).isEqualTo(1);
    assertThat(tripCopy.get(0)).isEqualTo(trip);
  }

  @Test
  void shouldGetPassengerDetails() {
    Long loggedInUserId = 1L;
    Long tripId = 4L;
    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);

    List<Integer> passengerIds = Arrays.asList(2, 3);
    List<Long> passengerIdsToLong = Arrays.asList(2L, 3L);

    Trip trip = Trip.builder().driverId(loggedInUserId).passengerIds(passengerIds).build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

    List<UserDetails> expected = Arrays
      .asList(UserDetails.builder().fullName("name1").build(), UserDetails.builder().fullName("name2").build());

    when(userService.getUserDetails(passengerIdsToLong)).thenReturn(expected);

    List<UserDetails> myPassengerDetails = tripService.getMyPassengerDetails(tripId);

    assertThat(myPassengerDetails).isEqualTo(expected);
  }

  @Test
  void shouldThrowExceptionWhenGettingPassengerDetailsIfUserIsNotADriver() {
    Long loggedInUserId = 1L;
    Long tripId = 5L;
    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);
    Trip trip = Trip.builder().driverId(loggedInUserId + 1).build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

    assertThrows(UnauthorizedException.class, () -> {
      tripService.getMyPassengerDetails(tripId);
    }, "You do not have permissions to view this");
  }

  @Test
  void shouldGetDriverDetails() {
    Long loggedInUserId = 1L;
    Long tripId = 5L;
    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);
    Trip trip = Trip.builder().driverId(loggedInUserId + 1)
      .passengerIds(Collections.singletonList(loggedInUserId.intValue())).build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));
    UserDetails userDetails = UserDetails.builder().fullName("Driver").build();
    when(userService.getUserDetails(Collections.singletonList(loggedInUserId + 1)))
      .thenReturn(Collections.singletonList(userDetails));

    UserDetails result = tripService.getDriverDetails(tripId);

    assertThat(result).isEqualTo(userDetails);
  }

  @Test
  void shouldThrowExceptionWhenGettingDriverDetailsIfUserIsNotAPassenger() {
    Long loggedInUserId = 1L;
    Long tripId = 5L;
    when(principalService.getLoggedInUserId()).thenReturn(loggedInUserId);
    Trip trip = Trip.builder().driverId(loggedInUserId + 1).passengerIds(new ArrayList<>()).build();
    when(tripRepository.findById(tripId)).thenReturn(Optional.of(trip));

    assertThrows(BadRequestException.class, () -> {
      tripService.getDriverDetails(tripId);
    }, "Trip does not contain customer");
  }
}