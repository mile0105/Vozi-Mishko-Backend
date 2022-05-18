package com.vozimishko.backend.document.service;

import com.vozimishko.backend.document.model.Document;
import com.vozimishko.backend.document.model.DocumentDto;
import com.vozimishko.backend.document.repository.DocumentRepository;
import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.exceptions.NotFoundException;
import com.vozimishko.backend.error.model.ErrorMessage;
import com.vozimishko.backend.security.PrincipalService;
import com.vozimishko.backend.trip.model.Trip;
import com.vozimishko.backend.trip.service.TripService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class DocumentService {
  private final DocumentRepository documentRepository;
  private final PrincipalService principalService;
  private final TripService tripService;

  public List<Document> getMyDocuments() {
    Long userId = principalService.getLoggedInUserId();
    return documentRepository.getDocumentsFromUser(userId);
  }

  public List<Document> getDocumentsFromTrip(Long tripId) {
    Trip trip = tripService.findByIdOrThrow(tripId);
    Long userId = principalService.getLoggedInUserId();
    if (!Objects.equals(trip.getDriverId(), userId)) {
      throw new BadRequestException(ErrorMessage.YOU_ARE_NOT_THE_DRIVER);
    }
    return documentRepository.getDocumentsFromTrip(tripId);
  }

  public Document addDocument(DocumentDto documentDto) {
    Long userId = principalService.getLoggedInUserId();
    Trip trip = tripService.findByIdOrThrow(documentDto.getTripId());
    Long numberOfDocumentsInTrip = documentRepository.getNumberOfDocumentsInTrip(documentDto.getTripId());

    if (trip.getMaximumNumberOfDocuments() <= numberOfDocumentsInTrip + 1) {
      throw new BadRequestException(ErrorMessage.TRIP_IS_FULL);
    }

    Document document = documentDto.toDbDocument(userId);
    return documentRepository.save(document);
  }

  public Document getDocumentByIdOrThrow(Long documentId) {
    Long userId = principalService.getLoggedInUserId();
    Document document = documentRepository.findById(documentId).orElseThrow(() -> new NotFoundException(ErrorMessage.DOCUMENT_NOT_FOUND));
    Trip trip = tripService.findByIdOrThrow(document.getTripId());

    if (!Objects.equals(document.getOwnerId(), userId) && !Objects.equals(trip.getDriverId(), userId)) {
      throw new BadRequestException(ErrorMessage.DOCUMENT_UNAVAILABLE);
    }
    return document;
  }
}
