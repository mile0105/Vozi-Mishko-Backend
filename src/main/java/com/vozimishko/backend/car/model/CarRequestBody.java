package com.vozimishko.backend.car.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CarRequestBody {

  @NotBlank(message = "Manufacturer name must not be empty")
  private String manufacturerName;
  @NotBlank(message = "Model name must not be empty")
  private String modelName;
  @NotNull(message = "Number of seats must not be empty")
  @Size(max = 30, message = "For the purpose of this application, you cannot have more than 30 passengers")
  private int numberOfSeats;
}
