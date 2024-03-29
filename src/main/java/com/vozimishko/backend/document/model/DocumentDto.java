package com.vozimishko.backend.document.model;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DocumentDto {

  @NotEmpty(message = "Please add the first name of your recipient")
  private String receiverFirstName;
  @NotEmpty(message = "Please add the phone number of your recipient")
  private String receiverPhoneNumber;
  @NotEmpty(message = "Please add the document format")
  private String format;
  @NotNull(message = "Please add the trip id")
  private Long tripId;

  public Document toDbDocument(Long ownerId) {
    return Document.builder()
      .receiverFirstName(receiverFirstName)
      .receiverPhoneNumber(receiverPhoneNumber)
      .format(format)
      .tripId(tripId)
      .ownerId(ownerId)
      .build();
  }
}
