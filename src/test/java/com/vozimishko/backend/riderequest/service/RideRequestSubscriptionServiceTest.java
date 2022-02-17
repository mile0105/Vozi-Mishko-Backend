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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RideRequestSubscriptionServiceTest {

  @Mock(lenient = true)
  private PrincipalService principalService;
  @Mock
  private CarService carService;
  @Mock
  private RideRequestRepository rideRequestRepository;
  @Mock
  private TripService tripService;
  @Captor
  private ArgumentCaptor<RideRequest> rideRequestArgumentCaptor;
  private RideRequestSubscriptionService rideRequestSubscriptionService;

  @BeforeEach
  void setUp() {
    when(principalService.getLoggedInUserId()).thenReturn(1L);
    rideRequestArgumentCaptor = ArgumentCaptor.forClass(RideRequest.class);
    rideRequestSubscriptionService = new RideRequestSubscriptionService(principalService, rideRequestRepository, carService, tripService);
  }

  @Test
  void shouldSubscribeToRequestOnExistingTrip() {
    Long rideRequestId = 1L;
    Long tripId = 2L;
    Long startCityId = 3L;
    Long endCityId = 4L;
    Long passengerId = 5L;
    LocalDateTime departureTime = LocalDateTime.of(2020, 1, 1, 0,0);
    RideRequestSubscriptionDto rideRequestSubscriptionDto = new RideRequestSubscriptionDto();
    rideRequestSubscriptionDto.setRideRequestId(rideRequestId);
    rideRequestSubscriptionDto.setTripId(tripId);
    when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.of(
      RideRequest.builder()
        .id(rideRequestId)
        .startCityId(startCityId)
        .endCityId(endCityId)
        .timeOfDeparture(departureTime)
        .passengerId(passengerId)
        .isConfirmed(false)
        .tripId(null)
        .build()
    ));
    Trip existingTrip = Trip.builder().driverId(1L).build();
    when(tripService.findByIdOrThrow(tripId)).thenReturn(existingTrip);

    rideRequestSubscriptionService.driverSubscribe(rideRequestSubscriptionDto);

    verify(tripService).validateTripSeats(existingTrip);
    verify(carService, never()).getLoggedInUserCars();
    verify(rideRequestRepository).save(rideRequestArgumentCaptor.capture());
    RideRequest savedValue = rideRequestArgumentCaptor.getValue();
    assertEquals(tripId, savedValue.getTripId());
    assertEquals(rideRequestId, savedValue.getId());
    assertEquals(departureTime, savedValue.getTimeOfDeparture());
    assertEquals(startCityId, savedValue.getStartCityId());
    assertEquals(endCityId, savedValue.getEndCityId());
    assertEquals(passengerId, savedValue.getPassengerId());
    assertNotNull(savedValue.getConfirmationExpiry());
    assertFalse(savedValue.isConfirmed());
  }

  @Test
  void shouldSubscribeToRequestOnNewTrip() {
    Long rideRequestId = 1L;
    Long carId = 2L;
    Long startCityId = 3L;
    Long endCityId = 4L;
    Long passengerId = 5L;
    Long tripId = 6L;
    LocalDateTime departureTime = LocalDateTime.of(2020, 1, 1, 0,0);
    RideRequestSubscriptionDto rideRequestSubscriptionDto = new RideRequestSubscriptionDto();
    rideRequestSubscriptionDto.setRideRequestId(rideRequestId);
    rideRequestSubscriptionDto.setCarId(carId);
    when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.of(
      RideRequest.builder()
        .id(rideRequestId)
        .startCityId(startCityId)
        .endCityId(endCityId)
        .timeOfDeparture(departureTime)
        .passengerId(passengerId)
        .isConfirmed(false)
        .tripId(null)
        .build()
    ));
    when(carService.getLoggedInUserCars()).thenReturn(Collections.singletonList(Car.builder().id(carId).build()));
    Trip newTrip = Trip.builder().id(tripId).carId(carId).startCityId(startCityId).endCityId(endCityId).driverId(1L).build();
    when(tripService.addTrip(TripRequestBody.builder().startCityId(startCityId).carId(carId).endCityId(endCityId).timeOfDeparture(departureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build())).thenReturn(newTrip);

    rideRequestSubscriptionService.driverSubscribe(rideRequestSubscriptionDto);

    verify(rideRequestRepository).save(rideRequestArgumentCaptor.capture());
    RideRequest savedValue = rideRequestArgumentCaptor.getValue();
    assertEquals(tripId, savedValue.getTripId());
    assertEquals(rideRequestId, savedValue.getId());
    assertEquals(departureTime, savedValue.getTimeOfDeparture());
    assertEquals(startCityId, savedValue.getStartCityId());
    assertEquals(endCityId, savedValue.getEndCityId());
    assertEquals(passengerId, savedValue.getPassengerId());
    assertNotNull(savedValue.getConfirmationExpiry());
    assertFalse(savedValue.isConfirmed());
  }

  @Test
  void shouldSubscribeToRequestIfExistingRequestLockHasExpired() {
    Long rideRequestId = 1L;
    Long carId = 2L;
    Long startCityId = 3L;
    Long endCityId = 4L;
    Long passengerId = 5L;
    Long tripId = 6L;
    LocalDateTime departureTime = LocalDateTime.of(2020, 1, 1, 0,0);
    RideRequestSubscriptionDto rideRequestSubscriptionDto = new RideRequestSubscriptionDto();
    rideRequestSubscriptionDto.setRideRequestId(rideRequestId);
    rideRequestSubscriptionDto.setCarId(carId);
    when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.of(
      RideRequest.builder()
        .id(rideRequestId)
        .startCityId(startCityId)
        .endCityId(endCityId)
        .timeOfDeparture(departureTime)
        .passengerId(passengerId)
        .isConfirmed(false)
        .tripId(7L)
        .confirmationExpiry(LocalDateTime.MIN)
        .build()
    ));
    when(carService.getLoggedInUserCars()).thenReturn(Collections.singletonList(Car.builder().id(carId).build()));
    Trip newTrip = Trip.builder().id(tripId).carId(carId).startCityId(startCityId).endCityId(endCityId).driverId(1L).build();
    when(tripService.addTrip(TripRequestBody.builder().startCityId(startCityId).carId(carId).endCityId(endCityId).timeOfDeparture(departureTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))).build())).thenReturn(newTrip);

    rideRequestSubscriptionService.driverSubscribe(rideRequestSubscriptionDto);

    verify(rideRequestRepository).save(rideRequestArgumentCaptor.capture());
    RideRequest savedValue = rideRequestArgumentCaptor.getValue();
    assertEquals(tripId, savedValue.getTripId());
    assertEquals(rideRequestId, savedValue.getId());
    assertEquals(departureTime, savedValue.getTimeOfDeparture());
    assertEquals(startCityId, savedValue.getStartCityId());
    assertEquals(endCityId, savedValue.getEndCityId());
    assertEquals(passengerId, savedValue.getPassengerId());
    assertNotNull(savedValue.getConfirmationExpiry());
    assertFalse(savedValue.isConfirmed());
  }

  @Test
  void shouldThrowExceptionIfSubscriptionDoesNotHaveCarNorTrip() {
    Long rideRequestId = 1L;
    RideRequestSubscriptionDto rideRequestSubscriptionDto = new RideRequestSubscriptionDto();
    rideRequestSubscriptionDto.setRideRequestId(rideRequestId);

    BadRequestException exception = assertThrows(BadRequestException.class, () -> rideRequestSubscriptionService.driverSubscribe(rideRequestSubscriptionDto));

    assertEquals(ErrorMessage.RIDE_REQUEST_SUBSCRIPTION_CAR_AND_TRIP_NULL, exception.getErrorMessage());
    verifyNoMutationOccured();
  }

  @Test
  void shouldThrowExceptionIfRideRequestCannotBeFound() {
    Long rideRequestId = 1L;
    Long tripId = 2L;
    when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.empty());
    RideRequestSubscriptionDto rideRequestSubscriptionDto = new RideRequestSubscriptionDto();
    rideRequestSubscriptionDto.setRideRequestId(rideRequestId);
    rideRequestSubscriptionDto.setTripId(tripId);

    NotFoundException exception = assertThrows(NotFoundException.class, () -> rideRequestSubscriptionService.driverSubscribe(rideRequestSubscriptionDto));

    assertEquals(ErrorMessage.RIDE_REQUEST_NOT_FOUND, exception.getErrorMessage());
    verifyNoMutationOccured();
  }

  @Test
  void shouldThrowExceptionIfRideRequestIsAlreadyConfirmed() {
    Long rideRequestId = 1L;
    Long tripId = 2L;
    when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.of(RideRequest.builder().tripId(1L).isConfirmed(true).build()));
    RideRequestSubscriptionDto rideRequestSubscriptionDto = new RideRequestSubscriptionDto();
    rideRequestSubscriptionDto.setRideRequestId(rideRequestId);
    rideRequestSubscriptionDto.setTripId(tripId);

    BadRequestException exception = assertThrows(BadRequestException.class, () -> rideRequestSubscriptionService.driverSubscribe(rideRequestSubscriptionDto));

    assertEquals(ErrorMessage.RIDE_REQUEST_HAS_SUBSCRIPTION, exception.getErrorMessage());
    verifyNoMutationOccured();
  }

  @Test
  void shouldThrowExceptionIfRideRequestAlreadyHasTrip() {
    Long rideRequestId = 1L;
    Long tripId = 2L;
    when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.of(RideRequest.builder().tripId(1L).confirmationExpiry(LocalDateTime.MAX).build()));
    RideRequestSubscriptionDto rideRequestSubscriptionDto = new RideRequestSubscriptionDto();
    rideRequestSubscriptionDto.setRideRequestId(rideRequestId);
    rideRequestSubscriptionDto.setTripId(tripId);

    BadRequestException exception = assertThrows(BadRequestException.class, () -> rideRequestSubscriptionService.driverSubscribe(rideRequestSubscriptionDto));

    assertEquals(ErrorMessage.RIDE_REQUEST_HAS_SUBSCRIPTION, exception.getErrorMessage());
    verifyNoMutationOccured();
  }

  @Test
  void shouldThrowExceptionIfYouPickANonExistingTrip() {
    Long rideRequestId = 1L;
    Long tripId = 2L;
    when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.of(RideRequest.builder().build()));
    RideRequestSubscriptionDto rideRequestSubscriptionDto = new RideRequestSubscriptionDto();
    rideRequestSubscriptionDto.setRideRequestId(rideRequestId);
    rideRequestSubscriptionDto.setTripId(tripId);
    when(tripService.findByIdOrThrow(tripId)).thenThrow(new NotFoundException(ErrorMessage.TRIP_NOT_FOUND));

    NotFoundException exception = assertThrows(NotFoundException.class, () -> rideRequestSubscriptionService.driverSubscribe(rideRequestSubscriptionDto));

    assertEquals(ErrorMessage.TRIP_NOT_FOUND, exception.getErrorMessage());
    verifyNoMutationOccured();
  }

  @Test
  void shouldThrowExceptionIfYouPickATripThatYouAreNotTheDriver() {
    Long rideRequestId = 1L;
    Long tripId = 2L;
    when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.of(RideRequest.builder().build()));
    RideRequestSubscriptionDto rideRequestSubscriptionDto = new RideRequestSubscriptionDto();
    rideRequestSubscriptionDto.setRideRequestId(rideRequestId);
    rideRequestSubscriptionDto.setTripId(tripId);

    Trip existingTrip = Trip.builder().driverId(2L).build();
    when(tripService.findByIdOrThrow(tripId)).thenReturn(existingTrip);

    BadRequestException exception = assertThrows(BadRequestException.class, () -> rideRequestSubscriptionService.driverSubscribe(rideRequestSubscriptionDto));

    assertEquals(ErrorMessage.YOU_ARE_NOT_THE_DRIVER, exception.getErrorMessage());
    verifyNoMutationOccured();
  }

  @Test
  void shouldThrowExcepitonIfYouPickATripThatIsNotValidated() {
    Long rideRequestId = 1L;
    Long tripId = 2L;
    when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.of(RideRequest.builder().build()));
    RideRequestSubscriptionDto rideRequestSubscriptionDto = new RideRequestSubscriptionDto();
    rideRequestSubscriptionDto.setRideRequestId(rideRequestId);
    rideRequestSubscriptionDto.setTripId(tripId);
    Trip existingTrip = Trip.builder().driverId(1L).build();
    when(tripService.findByIdOrThrow(tripId)).thenReturn(existingTrip);
    doThrow(new BadRequestException(ErrorMessage.TRIP_IS_FULL)).when(tripService).validateTripSeats(existingTrip);

    BadRequestException exception = assertThrows(BadRequestException.class, () -> rideRequestSubscriptionService.driverSubscribe(rideRequestSubscriptionDto));

    assertEquals(ErrorMessage.TRIP_IS_FULL, exception.getErrorMessage());
    verifyNoMutationOccured();
  }

  @Test
  void shouldThrowExceptionIfCarDoesNotBelongToYou() {
    Long rideRequestId = 1L;
    Long carId = 2L;
    when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.of(RideRequest.builder().build()));
    RideRequestSubscriptionDto rideRequestSubscriptionDto = new RideRequestSubscriptionDto();
    rideRequestSubscriptionDto.setRideRequestId(rideRequestId);
    rideRequestSubscriptionDto.setCarId(carId);
    when(carService.getLoggedInUserCars()).thenReturn(Collections.emptyList());

    BadRequestException exception = assertThrows(BadRequestException.class, () -> rideRequestSubscriptionService.driverSubscribe(rideRequestSubscriptionDto));
    assertEquals(ErrorMessage.CAR_UNAVAILABLE, exception.getErrorMessage());
    verifyNoMutationOccured();
  }

  private void verifyNoMutationOccured() {
    verify(rideRequestRepository, never()).save(any());
    verify(tripService, never()).addTrip(any());
  }
}
