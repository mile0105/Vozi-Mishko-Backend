package com.vozimishko.backend.riderequest;

import com.vozimishko.backend.riderequest.model.RideRequest;
import com.vozimishko.backend.riderequest.model.RideRequestDto;
import com.vozimishko.backend.riderequest.service.RideRequestService;
import com.vozimishko.backend.util.DateUtils;
import com.vozimishko.backend.util.models.EmptyResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Set;

@RestController(value = "/api/v1/riderequests")
@RequiredArgsConstructor
public class RideRequestController {

  private final RideRequestService rideRequestService;

  @PostMapping("")
  public ResponseEntity<EmptyResponse> addRideRequest(@Valid @RequestBody RideRequestDto rideRequest) {
    rideRequestService.addRideRequest(rideRequest);
    return ResponseEntity.ok(new EmptyResponse());
  }

  @GetMapping("")
  public ResponseEntity<Set<RideRequest>> getRideRequests(@RequestParam(name = "startCityId", required = false) Long startCityId,
                                                          @RequestParam(name = "endCityId", required = false) Long endCityId,
                                                          @RequestParam(name = "date", required = false) String dateString) {
    Set<RideRequest> rideRequests = rideRequestService.getRideRequests(startCityId, endCityId, DateUtils.parseDate(dateString));
    return ResponseEntity.ok(rideRequests);
  }
}
