package com.vozimishko.backend.util.models;

import lombok.RequiredArgsConstructor;
import java.util.EnumSet;

@RequiredArgsConstructor
public enum RequestLanguage {
  ENGLISH("en"),
  MACEDONIAN("mk"),
  ALBANIAN("al");

  private final String languageText;

  public static RequestLanguage findFromLanguageText(String languageText) {
    if (languageText == null) {
      return ENGLISH;
    }
    return EnumSet.allOf(RequestLanguage.class).stream().filter(em -> languageText.equals(em.languageText)).findFirst().orElse(ENGLISH);
  }

}
