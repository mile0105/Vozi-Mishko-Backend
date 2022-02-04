package com.vozimishko.backend.cities.model;

import com.vozimishko.backend.util.RequestUtils;
import com.vozimishko.backend.util.models.RequestLanguage;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.lang.NonNull;

@Data
@Table("city")
public class City implements Comparable<City> {

  @Id
  private Long id;
  private String englishName;
  private String macedonianName;
  private String albanianName;

  @Override
  public int compareTo(@NonNull City other) {
    RequestLanguage requestLanguage = RequestUtils.getRequestLanguage();
    switch (requestLanguage) {
      case ENGLISH: return englishName.compareTo(other.englishName);
      case MACEDONIAN: return macedonianName.compareTo(other.macedonianName);
      case ALBANIAN: return albanianName.compareTo(other.albanianName);
    }
    return 0;
  }
}
