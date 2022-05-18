package com.vozimishko.backend.trip;

import com.vozimishko.backend.document.model.Document;
import com.vozimishko.backend.document.service.DocumentService;
import com.vozimishko.backend.trip.model.Trip;
import com.vozimishko.backend.trip.model.TripRequestBody;
import com.vozimishko.backend.trip.service.TripService;
import com.vozimishko.backend.user.model.UserDetails;
import com.vozimishko.backend.util.DateUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/v1/trips")
@RequiredArgsConstructor
public class TripController {

  private final TripService tripService;
  private final DocumentService documentService;

  @PostMapping
  public ResponseEntity<Trip> addTrip(@RequestBody TripRequestBody tripRequestBody) {
    Trip result = tripService.addTrip(tripRequestBody);
    return ResponseEntity.ok(result);
  }

  @GetMapping
  public ResponseEntity<Set<Trip>> getTrips(@RequestParam(name = "startCityId", required = false) Long startCityId,
                                             @RequestParam(name = "endCityId", required = false) Long endCityId,
                                             @RequestParam(name = "date", required = false) String dateString) {
    Set<Trip> trips = tripService.fetchTrips(startCityId, endCityId, DateUtils.parseDate(dateString));
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

  @GetMapping("/{id}/documents")
  public ResponseEntity<List<Document>> getDocumentsFromTrip(@PathVariable(name = "id") Long tripId) {
    List<Document> documents = documentService.getDocumentsFromTrip(tripId);
    return ResponseEntity.ok(documents);
  }
}
