package com.vozimishko.backend.error.model;

import com.vozimishko.backend.util.models.RequestLanguage;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public enum ErrorMessage {
  INVALID_REQUEST("Invalid request", "Погрешно барање", ""),
  CAR_NOT_FOUND("Car not found", "Автомобилот не е пронајден", ""),
  CAR_UNAVAILABLE("Car unavailable", "Автомобилот не е достапен", ""),
  CITY_NOT_FOUND("City not found", "Градот не е пронајден", ""),
  TRIP_NOT_FOUND("Trip not found", "Превозот не е пронајден", ""),
  TRIP_SAME_CITIES("Start city can not be the same as end city", "Почетниот град не може да биде ист со крајниот град" , ""),
  TRIP_IS_FULL("Trip is full, please select another trip", "Превозот е полн, ве молиме одберете друг", ""),
  TRIP_ALREADY_CONTAINS_CUSTOMER("Trip already contains customer", "Превозот веќе го содржи патникот", ""),
  TRIP_DOES_NOT_CONTAINS_CUSTOMER("Trip does not contain customer", "Превозот не го содржи патникот", ""),
  NO_PERMISSIONS("You do not have permissions to view this", "Немате пристап", ""),
  DRIVER_CANNOT_SUBSCRIBE("Driver cannot further subscribe/unsubscribe from trip", "Возачот не може да се претплати/одплати од превозот", ""),
  USER_EXISTS_EMAIL("User with email already exists", "Веќе има корисник со тој е-маил", ""),
  USER_EXISTS_PHONE("User with phone number already exists", "Веќе има корисник со тој телефонски број", ""),
  INVALID_DATE("Invalid date, please enter the date in format YYYY-MM-DD", "Погрешен датум, ве молиме внесете го датумот во формат: ГГГГ-ММ-ДД", ""),
  SOMETHING_WENT_WRONG("Something went wrong, if this problem persists, please contact us", "Нешто не е во ред, ако продолжете да го добивате овој резултат, ве молиме контактирајте не", ""),
  EMPTY("","","");

  private final String englishVersion;
  private final String macedonianVersion;
  private final String albanianVersion;

  public String getFromLanguage(RequestLanguage language) {
    switch (language) {
      case ENGLISH: return englishVersion;
      case ALBANIAN: return albanianVersion;
      case MACEDONIAN: return macedonianVersion;
      default: return "";
    }
  }
}
