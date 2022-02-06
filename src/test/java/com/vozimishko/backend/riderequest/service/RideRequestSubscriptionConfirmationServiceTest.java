package com.vozimishko.backend.riderequest.service;

import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.exceptions.NotFoundException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.riderequest.model.RideRequest;
import com.vozimishko.backend.riderequest.repository.RideRequestRepository;
import com.vozimishko.backend.trip.service.TripService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class RideRequestSubscriptionConfirmationServiceTest {

  @Mock
  private RideRequestRepository rideRequestRepository;
  @Mock
  private TripService tripService;
  private RideRequestSubscriptionConfirmationService confirmationService;

  @BeforeEach
  void setUp() {
    confirmationService = new RideRequestSubscriptionConfirmationService(rideRequestRepository, tripService);
  }

  @Test
  void shouldConfirmSubscription() {
    Long rideRequestId = 1L;
    Long tripId = 2L;
    RideRequest rideRequest = RideRequest.builder().id(rideRequestId).tripId(tripId).confirmationExpiry(LocalDateTime.MAX).build();
    when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.of(rideRequest));

    confirmationService.confirmSubscription(rideRequestId);

    verify(tripService).subscribeToTrip(tripId);
    verify(rideRequestRepository).save(rideRequest.toBuilder().isConfirmed(true).build());
  }

  @Test
  void shouldDenySubscription() {
    Long rideRequestId = 1L;
    RideRequest rideRequest = RideRequest.builder().id(rideRequestId).tripId(2L).confirmationExpiry(LocalDateTime.MAX).build();
    when(rideRequestRepository.findById(rideRequestId)).thenReturn(Optional.of(rideRequest));

    confirmationService.denySubscription(rideRequestId);

    verify(tripService, never()).subscribeToTrip(any());
    verify(rideRequestRepository).save(rideRequest.toBuilder().tripId(null).confirmationExpiry(null).build());
  }

  @Test
  void shouldThrowExceptionIfRequestCannotBeFoundWhenConfirming() {
    when(rideRequestRepository.findById(1L)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () -> confirmationService.confirmSubscription(1L));

    assertEquals(ErrorMessage.RIDE_REQUEST_NOT_FOUND, exception.getErrorMessage());
    verify(tripService, never()).subscribeToTrip(any());
    verify(rideRequestRepository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfSubscriptionAlreadyConfirmedWhenConfirming() {
    RideRequest rideRequest = RideRequest.builder().isConfirmed(true).build();
    when(rideRequestRepository.findById(1L)).thenReturn(Optional.of(rideRequest));

    BadRequestException exception = assertThrows(BadRequestException.class, () -> confirmationService.confirmSubscription(1L));

    assertEquals(ErrorMessage.RIDE_REQUEST_CANNOT_BE_CONFIRMED, exception.getErrorMessage());
    verify(tripService, never()).subscribeToTrip(any());
    verify(rideRequestRepository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfSubscriptionDoesNotHaveTripWhenConfirming() {
    RideRequest rideRequest = RideRequest.builder().build();
    when(rideRequestRepository.findById(1L)).thenReturn(Optional.of(rideRequest));

    BadRequestException exception = assertThrows(BadRequestException.class, () -> confirmationService.confirmSubscription(1L));

    assertEquals(ErrorMessage.RIDE_REQUEST_CANNOT_BE_CONFIRMED, exception.getErrorMessage());
    verify(tripService, never()).subscribeToTrip(any());
    verify(rideRequestRepository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfSubscriptionConfirmationHasExpiredWhenConfirming() {
    RideRequest rideRequest = RideRequest.builder().tripId(1L).confirmationExpiry(LocalDateTime.MIN).build();
    when(rideRequestRepository.findById(1L)).thenReturn(Optional.of(rideRequest));

    BadRequestException exception = assertThrows(BadRequestException.class, () -> confirmationService.confirmSubscription(1L));

    assertEquals(ErrorMessage.RIDE_REQUEST_CANNOT_BE_CONFIRMED, exception.getErrorMessage());
    verify(tripService, never()).subscribeToTrip(any());
    verify(rideRequestRepository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfRequestCannotBeFoundWhenDenying() {
    when(rideRequestRepository.findById(1L)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () -> confirmationService.denySubscription(1L));

    assertEquals(ErrorMessage.RIDE_REQUEST_NOT_FOUND, exception.getErrorMessage());
    verify(rideRequestRepository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfSubscriptionAlreadyConfirmedWhenDenying() {
    RideRequest rideRequest = RideRequest.builder().isConfirmed(true).build();
    when(rideRequestRepository.findById(1L)).thenReturn(Optional.of(rideRequest));

    BadRequestException exception = assertThrows(BadRequestException.class, () -> confirmationService.denySubscription(1L));

    assertEquals(ErrorMessage.RIDE_REQUEST_CANNOT_BE_DENIED, exception.getErrorMessage());
    verify(rideRequestRepository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfSubscriptionDoesNotHaveTripWhenDenying() {
    RideRequest rideRequest = RideRequest.builder().build();
    when(rideRequestRepository.findById(1L)).thenReturn(Optional.of(rideRequest));

    BadRequestException exception = assertThrows(BadRequestException.class, () -> confirmationService.denySubscription(1L));

    assertEquals(ErrorMessage.RIDE_REQUEST_CANNOT_BE_DENIED, exception.getErrorMessage());
    verify(rideRequestRepository, never()).save(any());
  }

  @Test
  void shouldThrowExceptionIfSubscriptionConfirmationHasExpiredWhenDenying() {
    RideRequest rideRequest = RideRequest.builder().tripId(1L).confirmationExpiry(LocalDateTime.MIN).build();
    when(rideRequestRepository.findById(1L)).thenReturn(Optional.of(rideRequest));

    BadRequestException exception = assertThrows(BadRequestException.class, () -> confirmationService.denySubscription(1L));

    assertEquals(ErrorMessage.RIDE_REQUEST_CANNOT_BE_DENIED, exception.getErrorMessage());
    verify(rideRequestRepository, never()).save(any());
  }
}
