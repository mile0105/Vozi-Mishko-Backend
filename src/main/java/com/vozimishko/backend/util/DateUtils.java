package com.vozimishko.backend.util;

import com.vozimishko.backend.error.exceptions.BadRequestException;
import com.vozimishko.backend.error.model.ErrorMessage;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;

public final class DateUtils {
  public static LocalDate parseDate(String dateString) {
    if (dateString == null) {
      return null;
    }

    try {
      return LocalDate.parse(dateString);
    } catch (DateTimeParseException ex) {
      throw new BadRequestException(ErrorMessage.INVALID_DATE);
    }
  }
}
