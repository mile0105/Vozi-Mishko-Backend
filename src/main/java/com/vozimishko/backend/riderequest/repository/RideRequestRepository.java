package com.vozimishko.backend.riderequest.repository;

import com.vozimishko.backend.riderequest.model.RideRequest;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RideRequestRepository extends CrudRepository<RideRequest, Long> {
}
