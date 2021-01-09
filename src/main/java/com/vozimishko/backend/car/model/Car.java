package com.vozimishko.backend.car.model;

import lombok.Builder;
import lombok.Getter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Builder
@Table("cars")
public class Car {
  @Id
  private Long id;
  private String manufacturerName;
  private String modelName;
  private int numberOfSeats;
  private Long userId;
}
