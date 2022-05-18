package com.vozimishko.backend.document.model;


import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder(toBuilder = true)
@Table("documents")
@EqualsAndHashCode
public class Document {

  @Id
  private Long id;
  @Column("owner_id")
  private Long ownerId;
  @Column("receiver_first_name")
  private String receiverFirstName;
  @Column("receiver_phone_number")
  private String receiverPhoneNumber;
  @Column
  private String format;
  @Column("trip_id")
  private Long tripId;
}
