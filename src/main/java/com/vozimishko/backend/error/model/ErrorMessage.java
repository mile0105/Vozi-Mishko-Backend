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
  TRIP_DOES_NOT_CONTAIN_CUSTOMER("Trip does not contain customer", "Превозот не го содржи патникот", ""),
  NO_PERMISSIONS("You do not have permissions to view this", "Немате пристап", ""),
  DRIVER_CANNOT_SUBSCRIBE("Driver cannot further subscribe/unsubscribe from trip", "Возачот не може да се претплати/одплати од превозот", ""),
  YOU_ARE_NOT_THE_DRIVER("You are not the driver on this trip", "Вие не сте возач на тој превоз", ""),
  USER_EXISTS_EMAIL("User with email already exists", "Веќе има корисник со тој е-маил", ""),
  USER_EXISTS_PHONE("User with phone number already exists", "Веќе има корисник со тој телефонски број", ""),
  INVALID_DATE("Invalid date, please enter the date in format YYYY-MM-DD", "Погрешен датум, ве молиме внесете го датумот во формат: ГГГГ-ММ-ДД", ""),
  SOMETHING_WENT_WRONG("Something went wrong, if this problem persists, please contact us", "Нешто не е во ред, ако продолжете да го добивате овој резултат, ве молиме контактирајте не", ""),
  INVALID_CREDENTIALS("Invalid credentials","Погрешни креденцијали",""),
  RIDE_REQUEST_NOT_FOUND("Your ride request is not found","Вашата барана рута не е пронајдена",""),
  RIDE_REQUEST_HAS_SUBSCRIPTION("The ride request already has subscription","Вашата барана рута веќе има барање за превоз",""),
  RIDE_REQUEST_SUBSCRIPTION_CAR_AND_TRIP_NULL("Your ride request subscription must contain a car or an existing trip",
    "Вашата понуда за бараната рута нема валиден автомобил или превоз",""),
  RIDE_REQUEST_CANNOT_BE_CONFIRMED("The ride request can not be confirmed","Понудата за барана рута не може да се потврди" ,""),
  RIDE_REQUEST_CANNOT_BE_DENIED("The ride request can not be denied","Понудата за барана рута не може да се одбие" ,""),
  FREE_SPACES_EXCEED_CAR_CAPACITY("Free spaces exceed car capacity","Слободните места се повеќе од капацитетот на автомобилот" ,""),
  DOCUMENT_NOT_FOUND("The document is not found", "Документот не е пронајден",""),
  DOCUMENT_UNAVAILABLE("The document is not available", "Документот не е достапен", ""),
  PROFILE_NOT_COMPLETE("Your profile is not complete","Вашиот профил не е комплетиран",""),
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
