package com.vozimishko.backend.trip;

import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.trip.model.Trip;
import com.vozimishko.backend.trip.model.TripApi;
import com.vozimishko.backend.trip.service.TripService;
import com.vozimishko.backend.user.model.UserDetails;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

  private final TripService tripService;

  @PostMapping
  public ResponseEntity<Trip> addTrip(@RequestBody TripApi tripApi) {
    Trip result = tripService.addTrip(tripApi);
    return ResponseEntity.ok(result);
  }

  @GetMapping
  public ResponseEntity<Set<Trip>> getTrips(@RequestParam(name = "start", required = false) String start,
                                             @RequestParam(name = "end", required = false) String end,
                                             @RequestParam(name = "date", required = false) String dateString) {
    Set<Trip> trips = tripService.fetchTrips(start, end, parseDate(dateString));
    return ResponseEntity.ok(trips);
  }

  @GetMapping("/{id}")
  public ResponseEntity<Trip> getTripById(@PathVariable(name = "id") Long tripId) {
    Trip trip = tripService.findByIdOrThrow(tripId);
    return ResponseEntity.ok(trip);
  }

  @PatchMapping("/{id}/subscribe")
  public ResponseEntity<Trip> subscribeToTrip(@PathVariable(name = "id") Long tripId) {
    Trip trip = tripService.subscribeToTrip(tripId);
    return ResponseEntity.ok(trip);
  }

  @PatchMapping("/{id}/unsubscribe")
  public ResponseEntity<Trip> unsubscribeFromTrip(@PathVariable(name = "id") Long tripId) {
    Trip trip = tripService.unsubscribeFromTrip(tripId);
    return ResponseEntity.ok(trip);
  }

  @GetMapping("/my/driver")
  public ResponseEntity<Set<Trip>> getTripsWhereIDrive() {
    Set<Trip> trips = tripService.getTripsWhereIDrive();
    return ResponseEntity.ok(trips);
  }

  @GetMapping("/my/customer")
  public ResponseEntity<Set<Trip>> getTripsWhereIAmSubscribed() {
    Set<Trip> trips = tripService.getTripsWhereIAmSubscribed();
    return ResponseEntity.ok(trips);
  }

  @GetMapping("/{id}/details")
  public ResponseEntity<List<UserDetails>> getTripDetailsDriverView(@PathVariable(name = "id") Long tripId) {
    List<UserDetails> myCustomers = tripService.getMyPassengerDetails(tripId);
    return ResponseEntity.ok(myCustomers);
  }

  @GetMapping("/{id}/driver/details")
  public ResponseEntity<UserDetails> getDriverDetails(@PathVariable(name = "id") Long tripId) {
    UserDetails driverDetails = tripService.getDriverDetails(tripId);
    return ResponseEntity.ok(driverDetails);
  }

  private LocalDate parseDate(String dateString) {
    if (dateString == null) {
      return null;
    }

    try {
      return LocalDate.parse(dateString);
    } catch (DateTimeParseException ex) {
      throw new BadRequestException(ErrorMessage.INVALID_DATE);
    }
  }

}
