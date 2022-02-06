package com.vozimishko.backend.riderequest;

import com.vozimishko.backend.riderequest.model.RideRequest;
import com.vozimishko.backend.riderequest.model.RideRequestDto;
import com.vozimishko.backend.riderequest.model.RideRequestSubscriptionDto;
import com.vozimishko.backend.riderequest.service.RideRequestService;
import com.vozimishko.backend.riderequest.service.RideRequestSubscriptionConfirmationService;
import com.vozimishko.backend.riderequest.service.RideRequestSubscriptionService;
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
  private final RideRequestSubscriptionService rideRequestSubscriptionService;
  private final RideRequestSubscriptionConfirmationService rideRequestSubscriptionConfirmationService;

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

  @PutMapping("/subscribe")
  public ResponseEntity<EmptyResponse> subscribeToRideRequest(@Valid @RequestBody RideRequestSubscriptionDto rideRequestSubscriptionDto) {

    rideRequestSubscriptionService.driverSubscribe(rideRequestSubscriptionDto);
    return ResponseEntity.ok(new EmptyResponse());
  }

  @PatchMapping("/{id}/confirm")
  public ResponseEntity<EmptyResponse> confirmSubscriptionToRideRequest(@PathVariable(name = "id") Long rideRequestId) {
    rideRequestSubscriptionConfirmationService.confirmSubscription(rideRequestId);
    return ResponseEntity.ok(new EmptyResponse());
  }

  @PatchMapping("/{id}/deny")
  public ResponseEntity<EmptyResponse> denySubscriptionToRideRequest(@PathVariable(name = "id") Long rideRequestId) {
    rideRequestSubscriptionConfirmationService.denySubscription(rideRequestId);
    return ResponseEntity.ok(new EmptyResponse());
  }
}
