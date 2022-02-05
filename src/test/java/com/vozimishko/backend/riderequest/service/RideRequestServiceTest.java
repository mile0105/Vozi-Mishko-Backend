package com.vozimishko.backend.riderequest.service;

import com.vozimishko.backend.cities.model.City;
import com.vozimishko.backend.cities.service.CityService;
import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.riderequest.model.RideRequest;
import com.vozimishko.backend.riderequest.model.RideRequestDto;
import com.vozimishko.backend.riderequest.repository.RideRequestRepository;
import com.vozimishko.backend.security.PrincipalService;
import com.vozimishko.backend.trip.model.Trip;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RideRequestServiceTest {

  @Mock(lenient = true)
  private PrincipalService principalService;

  @Mock
  private RideRequestRepository rideRequestRepository;

  @Mock
  private CityService cityService;

  @Captor
  private ArgumentCaptor<RideRequest> rideRequestArgumentCaptor;

  private RideRequestService rideRequestService;

  private LocalDateTime earlierRideRequestTime;
  private LocalDateTime middleRideRequestTime;
  private LocalDateTime laterRideRequestTime;

  @BeforeEach
  void setUp() {
    earlierRideRequestTime = LocalDateTime.of(2021, 1, 1, 0, 0);
    middleRideRequestTime = LocalDateTime.of(2021, 1, 1, 1, 0);
    laterRideRequestTime = LocalDateTime.of(2021, 1, 2, 0, 0);
    rideRequestArgumentCaptor = ArgumentCaptor.forClass(RideRequest.class);
    when(principalService.getLoggedInUserId()).thenReturn(1L);
    rideRequestService = new RideRequestService(principalService, cityService, rideRequestRepository);
  }

  @Test
  void shouldTestAddingTheRideRequest() {
    RideRequestDto rideRequestDto = new RideRequestDto();
    rideRequestDto.setTimeOfDeparture("2020-01-01 00:00:00");
    rideRequestDto.setStartCityId(1L);
    rideRequestDto.setEndCityId(2L);

    rideRequestService.addRideRequest(rideRequestDto);

    verify(rideRequestRepository).save(rideRequestArgumentCaptor.capture());

    RideRequest rideRequest = rideRequestArgumentCaptor.getValue();

    assertEquals(1L, rideRequest.getPassengerId());
    assertEquals(1L, rideRequest.getStartCityId());
    assertEquals(2L, rideRequest.getEndCityId());
    assertEquals(LocalDateTime.of(2020,1,1,0,0,0), rideRequest.getTimeOfDeparture());
    assertFalse(rideRequest.isConfirmed());
    assertNull(rideRequest.getTripId());
  }

  @Test
  void shouldThrowBadRequestExceptionIfStartCityIsNotFound() {
    RideRequestDto rideRequestDto = new RideRequestDto();
    rideRequestDto.setStartCityId(1L);
    when(cityService.findByIdOrThrow(1L)).thenThrow(new BadRequestException(ErrorMessage.CITY_NOT_FOUND));

    BadRequestException exception = assertThrows(BadRequestException.class, () -> rideRequestService.addRideRequest(rideRequestDto));

    assertEquals(ErrorMessage.CITY_NOT_FOUND, exception.getErrorMessage());
  }

  @Test
  void shouldThrowBadRequestExceptionIfEndCityIsNotFound() {
    RideRequestDto rideRequestDto = new RideRequestDto();
    rideRequestDto.setStartCityId(1L);
    rideRequestDto.setEndCityId(2L);
    when(cityService.findByIdOrThrow(1L)).thenReturn(new City());
    when(cityService.findByIdOrThrow(2L)).thenThrow(new BadRequestException(ErrorMessage.CITY_NOT_FOUND));

    BadRequestException exception = assertThrows(BadRequestException.class, () -> rideRequestService.addRideRequest(rideRequestDto));

    assertEquals(ErrorMessage.CITY_NOT_FOUND, exception.getErrorMessage());
  }

  @Test
  void shouldThrowBadRequestExceptionIfCitiesAreTheSame() {
    RideRequestDto rideRequestDto = new RideRequestDto();
    rideRequestDto.setStartCityId(1L);
    rideRequestDto.setEndCityId(1L);

    BadRequestException exception = assertThrows(BadRequestException.class, () -> rideRequestService.addRideRequest(rideRequestDto));

    assertEquals(ErrorMessage.TRIP_SAME_CITIES, exception.getErrorMessage());
  }

  @Test
  void shouldTestFetchingRideRequestsOnGivenDateWhenDefiningStartAndEnd() {
    Long start = 1L;
    Long end = 2L;

    RideRequest earliestRideRequest = RideRequest.builder().timeOfDeparture(earlierRideRequestTime).build();
    RideRequest middleRideRequest = RideRequest.builder().timeOfDeparture(middleRideRequestTime).build();
    RideRequest latestRideRequest = RideRequest.builder().timeOfDeparture(laterRideRequestTime).build();
    Set<RideRequest> rideRequests = new HashSet<>(Arrays.asList(latestRideRequest, earliestRideRequest, middleRideRequest));

    when(rideRequestRepository.getUnconfirmedRideRequestsByOriginAndDestination(start, end)).thenReturn(new HashSet<>(rideRequests));

    Set<RideRequest> result = rideRequestService.getRideRequests(start, end, LocalDate.of(2021, 1, 1));

    List<RideRequest> rideRequestCopy = new ArrayList<>(result);
    assertThat(result.size()).isEqualTo(2);
    assertThat(rideRequestCopy.get(0)).isEqualTo(middleRideRequest);
    assertThat(rideRequestCopy.get(1)).isEqualTo(earliestRideRequest);
  }

  @Test
  void shouldTestFetchingRideRequestsWhenDefiningStartAndEnd() {
    Long start = 1L;
    Long end = 2L;

    RideRequest earliestRideRequest = RideRequest.builder().timeOfDeparture(earlierRideRequestTime).build();
    RideRequest middleRideRequest = RideRequest.builder().timeOfDeparture(middleRideRequestTime).build();
    RideRequest latestRideRequest = RideRequest.builder().timeOfDeparture(laterRideRequestTime).build();
    Set<RideRequest> rideRequests = new HashSet<>(Arrays.asList(latestRideRequest, earliestRideRequest, middleRideRequest));

    when(rideRequestRepository.getUnconfirmedRideRequestsByOriginAndDestination(start, end)).thenReturn(new HashSet<>(rideRequests));

    Set<RideRequest> result = rideRequestService.getRideRequests(start, end, null);

    List<RideRequest> rideRequestCopy = new ArrayList<>(result);
    assertThat(result.size()).isEqualTo(3);
    assertThat(rideRequestCopy.get(0)).isEqualTo(latestRideRequest);
    assertThat(rideRequestCopy.get(1)).isEqualTo(middleRideRequest);
    assertThat(rideRequestCopy.get(2)).isEqualTo(earliestRideRequest);
  }


  @Test
  void shouldTestFetchingAllRideRequestsOnGivenDate() {
    RideRequest earliestRideRequest = RideRequest.builder().timeOfDeparture(earlierRideRequestTime).build();
    RideRequest middleRideRequest = RideRequest.builder().timeOfDeparture(middleRideRequestTime).build();
    RideRequest latestRideRequest = RideRequest.builder().timeOfDeparture(laterRideRequestTime).build();
    Set<RideRequest> rideRequests = new HashSet<>(Arrays.asList(latestRideRequest, earliestRideRequest, middleRideRequest));

    when(rideRequestRepository.findAll()).thenReturn(new HashSet<>(rideRequests));

    Set<RideRequest> result = rideRequestService.getRideRequests(null, null, LocalDate.of(2021, 1, 1));

    List<RideRequest> rideRequestCopy = new ArrayList<>(result);
    assertThat(result.size()).isEqualTo(2);
    assertThat(rideRequestCopy.get(0)).isEqualTo(middleRideRequest);
    assertThat(rideRequestCopy.get(1)).isEqualTo(earliestRideRequest);
  }

  @Test
  void shouldTestFetchingAllRideRequests() {
    RideRequest earliestRideRequest = RideRequest.builder().timeOfDeparture(earlierRideRequestTime).build();
    RideRequest middleRideRequest = RideRequest.builder().timeOfDeparture(middleRideRequestTime).build();
    RideRequest latestRideRequest = RideRequest.builder().timeOfDeparture(laterRideRequestTime).build();
    Set<RideRequest> rideRequests = new HashSet<>(Arrays.asList(latestRideRequest, earliestRideRequest, middleRideRequest));

    when(rideRequestRepository.findAll()).thenReturn(new HashSet<>(rideRequests));

    Set<RideRequest> result = rideRequestService.getRideRequests(null, null, null);

    List<RideRequest> rideRequestCopy = new ArrayList<>(result);
    assertThat(result.size()).isEqualTo(3);
    assertThat(rideRequestCopy.get(0)).isEqualTo(latestRideRequest);
    assertThat(rideRequestCopy.get(1)).isEqualTo(middleRideRequest);
    assertThat(rideRequestCopy.get(2)).isEqualTo(earliestRideRequest);
  }
}
