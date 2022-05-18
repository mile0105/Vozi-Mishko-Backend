package com.vozimishko.backend.document;

import com.vozimishko.backend.document.model.Document;
import com.vozimishko.backend.document.model.DocumentDto;
import com.vozimishko.backend.document.service.DocumentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/documents")
@RequiredArgsConstructor
public class DocumentController {

  private final DocumentService documentService;

  @GetMapping("/my")
  public ResponseEntity<List<Document>> getMyDocuments() {
    List<Document> myDocuments = documentService.getMyDocuments();
    return ResponseEntity.ok(myDocuments);
  }

  @PostMapping("")
  public ResponseEntity<Document> addDocument(@RequestBody DocumentDto documentDto) {
    Document newDocument = documentService.addDocument(documentDto);
    return ResponseEntity.ok(newDocument);
  }

  @GetMapping("{id}")
  public ResponseEntity<Document> getDocumentById(@PathVariable(name = "id") Long documentId) {
    Document document = documentService.getDocumentByIdOrThrow(documentId);
    return ResponseEntity.ok(document);
  }
}
