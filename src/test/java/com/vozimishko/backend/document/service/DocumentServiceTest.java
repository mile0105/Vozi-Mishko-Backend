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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
class DocumentServiceTest {

  @Mock
  private DocumentRepository documentRepository;
  @Mock
  private PrincipalService principalService;
  @Mock
  private TripService tripService;

  private DocumentService documentService;

  private final Long tripId = 1L;

  @BeforeEach
  void setUp() {
    when(principalService.getLoggedInUserId()).thenReturn(1L);
    documentService = new DocumentService(documentRepository, principalService, tripService);
  }

  @Test
  void shouldReturnAllDocumentsBelongingToUser() {
    List<Document> documents = Collections.singletonList(Document.builder().id(2L).ownerId(1L).build());
    when(documentRepository.getDocumentsFromUser(1L)).thenReturn(documents);

    List<Document> myDocuments = documentService.getMyDocuments();

    assertEquals(myDocuments, documents);
  }

  @Test
  void shouldReturnDocumentsFromTrip() {
    List<Document> documents = Collections.singletonList(Document.builder().id(2L).ownerId(1L).build());
    Trip trip = Trip.builder().id(tripId).driverId(1L).build();
    when(tripService.findByIdOrThrow(tripId)).thenReturn(trip);
    when(documentRepository.getDocumentsFromTrip(tripId)).thenReturn(documents);

    List<Document> documentsFromTrip = documentService.getDocumentsFromTrip(tripId);

    assertEquals(documentsFromTrip, documents);
  }

  @Test
  void shouldThrowExceptionIfTryingToSeeDocumentsFromTripWhenYouAreNotADriver() {
    Trip trip = Trip.builder().id(tripId).driverId(2L).build();
    when(tripService.findByIdOrThrow(tripId)).thenReturn(trip);

    BadRequestException exception = assertThrows(BadRequestException.class, () -> documentService.getDocumentsFromTrip(tripId));

    assertEquals(ErrorMessage.YOU_ARE_NOT_THE_DRIVER, exception.getErrorMessage());
    verify(documentRepository, never()).getDocumentsFromTrip(any());
  }

  @Test
  void shouldAddDocumentToTrip() {
    Trip trip = Trip.builder().id(tripId).numberOfDocuments(0).maximumNumberOfDocuments(10).driverId(1L).build();
    when(tripService.findByIdOrThrow(tripId)).thenReturn(trip);
    DocumentDto documentDto = DocumentDto.builder().tripId(tripId).build();
    Document transformedDocument = Document.builder().tripId(tripId).ownerId(1L).build();
    Document savedDocument = Document.builder().id(1L).ownerId(1L).tripId(tripId).build();
    when(documentRepository.save(transformedDocument)).thenReturn(savedDocument);

    Document result = documentService.addDocument(documentDto);

    assertEquals(savedDocument, result);
    verify(tripService).updateTrip(tripId, trip.toBuilder().numberOfDocuments(1).build());
  }

  @Test
  void shouldThrowExceptionWhenTripIsFull() {
    Trip trip = Trip.builder().id(tripId).numberOfDocuments(10).maximumNumberOfDocuments(10).driverId(1L).build();
    when(tripService.findByIdOrThrow(tripId)).thenReturn(trip);
    DocumentDto documentDto = DocumentDto.builder().tripId(tripId).build();

    BadRequestException exception = assertThrows(BadRequestException.class, () -> documentService.addDocument(documentDto));

    assertEquals(ErrorMessage.TRIP_IS_FULL, exception.getErrorMessage());
    verify(documentRepository, never()).save(any());
  }

  @Test
  void shouldGetDocumentById() {
    Document document = Document.builder().id(1L).ownerId(1L).tripId(tripId).build();
    Trip trip = Trip.builder().id(tripId).driverId(1L).build();
    when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
    when(tripService.findByIdOrThrow(1L)).thenReturn(trip);

    Document result = documentService.getDocumentByIdOrThrow(1L);

    assertEquals(document, result);
  }

  @Test
  void shouldThrowExceptionIfDocumentIsNotFound() {
    when(documentRepository.findById(1L)).thenReturn(Optional.empty());

    NotFoundException exception = assertThrows(NotFoundException.class, () -> documentService.getDocumentByIdOrThrow(1L));

    assertEquals(ErrorMessage.DOCUMENT_NOT_FOUND, exception.getErrorMessage());
  }

  @Test
  void shouldThrowExceptionIfDocumentOwnerIsNotTheUserAndIsNotTheTripDriver() {
    Document document = Document.builder().id(1L).ownerId(2L).tripId(tripId).build();
    Trip trip = Trip.builder().id(tripId).driverId(3L).build();
    when(documentRepository.findById(1L)).thenReturn(Optional.of(document));
    when(tripService.findByIdOrThrow(1L)).thenReturn(trip);

    BadRequestException exception = assertThrows(BadRequestException.class, () -> documentService.getDocumentByIdOrThrow(1L));

    assertEquals(ErrorMessage.DOCUMENT_UNAVAILABLE, exception.getErrorMessage());
  }
}
