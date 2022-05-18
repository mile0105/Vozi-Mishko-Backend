package com.vozimishko.backend.document.repository;

import com.vozimishko.backend.document.model.Document;
import org.springframework.data.jdbc.repository.query.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DocumentRepository extends CrudRepository<Document, Long> {

  @Query("select * from documents where trip_id = :tripId")
  List<Document> getDocumentsFromTrip(@Param("tripId") Long tripId);

  @Query("select * from documents where owner_id = :userId")
  List<Document> getDocumentsFromUser(@Param("userId") Long userId);

  @Query("select count(*) from documents where trip_id = :tripId")
  Long getNumberOfDocumentsInTrip(@Param("tripId") Long tripId);

}
