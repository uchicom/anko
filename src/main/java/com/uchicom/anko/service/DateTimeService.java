package com.uchicom.anko.service;

import java.time.LocalDate;
import java.time.LocalDateTime;

import javax.inject.Inject;

import com.uchicom.anko.Constants;

public class DateTimeService {
  @Inject
  public DateTimeService(){}
    /**
   * 現在日を取得します.
   *
   * @return 現在日
   */
  public LocalDate getLocalDate() {
    return LocalDate.now(Constants.ZONE_ID);
  }

  /**
   * 現在日時を取得します.
   *
   * @return 現在日時
   */
  public LocalDateTime getLocalDateTime() {
    return LocalDateTime.now(Constants.ZONE_ID);
  }
}
