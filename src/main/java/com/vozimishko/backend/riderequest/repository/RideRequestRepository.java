package com.vozimishko.backend.riderequest.repository;

import com.vozimishko.backend.riderequest.model.RideRequest;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Set;

@Repository
public interface RideRequestRepository extends CrudRepository<RideRequest, Long> {

  @Query("select * from ride_requests where start_city_id = :start_city_id and end_city_id = :end_city_id and is_confirmed = false")
  Set<RideRequest> getUnconfirmedRideRequestsByOriginAndDestination(@Param("start_city_id") Long startCityId, @Param("end_city_id") Long endCityId);

  @Query("select count(*) from ride_requests where trip_id = :trip_id and is_confirmed = false and confirmation_expiry > now()")
  Long getNumberOfUnconfirmedRideRequestsByTripId(@Param("trip_id") Long tripId);


  @Query("select passenger_id from ride_requests where trip_id = :trip_id and is_confirmed = false and confirmation_expiry > now()")
  Set<Long> getPassengerIdsFromUnconfirmedRideRequestsByTripId(@Param("trip_id") Long tripId);
}
