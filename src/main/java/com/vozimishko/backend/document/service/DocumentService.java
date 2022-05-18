package com.vozimishko.backend.document.service;

import com.vozimishko.backend.document.model.Document;
import com.vozimishko.backend.document.model.DocumentDto;
import com.vozimishko.backend.document.repository.DocumentRepository;
import com.vozimishko.backend.security.PrincipalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentService {
  private final DocumentRepository documentRepository;
  private final PrincipalService principalService;

  public List<Document> getMyDocuments() {
    //todo implement

    return new ArrayList<>();
  }

  public Document addDocument(DocumentDto documentDto) {
    //todo implement
    return null;
  }

  public Document getDocumentById(Long documentId) {
    //todo implement
    return null;
  }
}
