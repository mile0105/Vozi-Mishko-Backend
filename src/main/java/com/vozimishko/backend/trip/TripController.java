package com.vozimishko.backend.trip;

import com.vozimishko.backend.trip.model.Trip;
import com.vozimishko.backend.trip.model.TripApi;
import com.vozimishko.backend.trip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
  public ResponseEntity<List<Trip>> getTrips(@RequestParam(name = "start") String start,
                                             @RequestParam(name = "end") String end) {
    List<Trip> trips = tripService.fetchTrips(start, end);
    return ResponseEntity.ok(trips);
  }

  @PatchMapping("/{id}")
  public ResponseEntity<Trip> subscribeToTrip(@PathVariable(name = "id") Long tripId) {
    Trip trip = tripService.subscribeToTrip(tripId);
    return ResponseEntity.ok(trip);
  }

}
