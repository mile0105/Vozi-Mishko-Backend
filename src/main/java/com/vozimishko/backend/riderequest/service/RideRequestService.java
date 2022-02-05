package com.vozimishko.backend.riderequest.service;


import com.vozimishko.backend.cities.service.CityService;
import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.riderequest.model.RideRequest;
import com.vozimishko.backend.riderequest.model.RideRequestDto;
import com.vozimishko.backend.riderequest.repository.RideRequestRepository;
import com.vozimishko.backend.security.PrincipalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class RideRequestService {

  private final PrincipalService principalService;
  private final CityService cityService;
  private final RideRequestRepository rideRequestRepository;

  public void addRideRequest(RideRequestDto rideRequestDto) {

    Long loggedInUserId = principalService.getLoggedInUserId();

    validateRideRequest(rideRequestDto);

    RideRequest rideRequest = rideRequestDto.toDbModel(loggedInUserId);
    rideRequestRepository.save(rideRequest);
  }

  public Set<RideRequest> getRideRequests(Long startCityId, Long endCityId, LocalDate dateOfDesiredTrip) {

    Set<RideRequest> rideRequests = null;

    if (startCityId != null && endCityId != null) {
      rideRequests = rideRequestRepository.getUnconfirmedRideRequestsByOriginAndDestination(startCityId, endCityId);
    }

    if (startCityId == null && endCityId == null) {
      rideRequests = StreamSupport.stream(rideRequestRepository.findAll().spliterator(), false).collect(Collectors.toSet());
    }

    if (rideRequests == null) {
      throw new BadRequestException(ErrorMessage.EMPTY);
    }

    return filterRideRequestsByDateAndSort(rideRequests, dateOfDesiredTrip);

  }

  public Long getNumberOfUnconfirmedRideRequestsForTrip(Long tripId) {
    return rideRequestRepository.getNumberOfUnconfirmedRideRequestsByTripId(tripId);
  }

  private void validateRideRequest(RideRequestDto rideRequestDto) {
    cityService.findByIdOrThrow(rideRequestDto.getStartCityId());
    cityService.findByIdOrThrow(rideRequestDto.getEndCityId());

    if (Objects.equals(rideRequestDto.getStartCityId(), rideRequestDto.getEndCityId())) {
      throw new BadRequestException(ErrorMessage.TRIP_SAME_CITIES);
    }
  }

  private Set<RideRequest> filterRideRequestsByDateAndSort(Set<RideRequest> rideRequests, LocalDate dateOfDesiredTrip) {
    if (dateOfDesiredTrip != null) {
      return rideRequests.stream().filter(trip -> trip.getTimeOfDeparture().toLocalDate().equals(dateOfDesiredTrip)).collect(Collectors.toCollection(TreeSet::new));
    }

    return new TreeSet<>(rideRequests);
  }
}
