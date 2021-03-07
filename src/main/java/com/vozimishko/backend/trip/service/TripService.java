package com.vozimishko.backend.trip.service;

import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.exceptions.NotFoundException;
import com.vozimishko.backend.security.PrincipalService;
import com.vozimishko.backend.trip.model.Trip;
import com.vozimishko.backend.trip.model.TripApi;
import com.vozimishko.backend.trip.repository.TripRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

@Service
@RequiredArgsConstructor
public class TripService {

  private final TripRepository tripRepository;
  private final TripMapper mapper;
  private final PrincipalService principalService;

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

    //todo optimize... maybe? xD

    Long loggedInUserId = principalService.getLoggedInUserId();

    Trip trip = findByIdOrThrow(tripId);

    List<Long> updatedPassengerIds = new ArrayList<>(trip.getPassengerIds());

    updatedPassengerIds.add(loggedInUserId);
    Trip newTrip = trip.toBuilder().passengerIds(updatedPassengerIds).build();

    return tripRepository.save(newTrip);
  }

  public Trip findByIdOrThrow(Long tripId) {
    return tripRepository.findById(tripId).orElseThrow(() -> new NotFoundException("Trip not found"));
  }

}
