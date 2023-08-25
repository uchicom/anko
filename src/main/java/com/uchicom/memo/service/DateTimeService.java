// (C) 2023 uchicom
package com.uchicom.memo.service;

import com.uchicom.memo.Constants;
import java.time.LocalDate;
import java.time.LocalDateTime;
import javax.inject.Inject;

public class DateTimeService {
  @Inject
  public DateTimeService() {}
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
