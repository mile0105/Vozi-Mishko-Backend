package com.vozimishko.backend.trip.service;

import com.vozimishko.backend.trip.model.Trip;
import com.vozimishko.backend.trip.model.TripRequestBody;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

@Component
public class TripMapper {

  public Trip mapToDbModelForAddition(TripRequestBody tripRequestBody, Long driverId) {

    LocalDateTime localDateTime = LocalDateTime
      .parse(tripRequestBody.getTimeOfDeparture(), DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));

    return Trip.builder()
      .startCityId(tripRequestBody.getStartCityId())
      .endCityId(tripRequestBody.getEndCityId())
      .timeOfDeparture(localDateTime)
      .carId(tripRequestBody.getCarId())
      .driverId(driverId)
      .passengerIds(new ArrayList<>())
      .tripPrice(tripRequestBody.getTripPrice())
      .documentPrice(tripRequestBody.getDocumentPrice() != null ? tripRequestBody.getDocumentPrice() : 0)
      .maximumNumberOfDocuments(tripRequestBody.getMaximumNumberOfDocuments() != null ? tripRequestBody.getMaximumNumberOfDocuments() : 0)
      .build();
  }
}
