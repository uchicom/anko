// (C) 2023 uchicom
package com.uchicom.tracker.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iciql.Iciql.IQColumn;
import java.time.LocalDateTime;

public abstract class AbstractTable {

  /** ID. */
  @IQColumn(primaryKey = true, autoIncrement = true, nullable = false)
  public Long id;

  /** 登録者. */
  @IQColumn(length = 256, nullable = false)
  public String inserted;

  /** 登録日時. */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @IQColumn(nullable = false)
  public LocalDateTime insert_datetime;

  /** 更新回数. */
  @IQColumn(nullable = false)
  public Integer update_seq;

  /** 更新者. */
  @IQColumn(length = 256)
  public String updated;

  /** 更新日時. */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @IQColumn
  public LocalDateTime update_datetime;
}
