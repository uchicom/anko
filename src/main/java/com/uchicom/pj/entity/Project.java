// (C) 2025 uchicom
package com.uchicom.pj.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;
import java.time.LocalDate;

/** プロジェクト. */
@IQSchema("pj")
@IQTable(name = "project", inheritColumns = true)
public class Project extends AbstractTable {

  /** アカウントID. */
  @IQColumn(nullable = false)
  public Long account_id;

  /** 顧客ID. */
  @IQColumn(nullable = false)
  public Long customer_id;

  /** 開始予定日. */
  @JsonFormat(pattern = "yyyy-MM-dd")
  @IQColumn
  public LocalDate start_schedule_date;

  /** 終了予定日. */
  @JsonFormat(pattern = "yyyy-MM-dd")
  @IQColumn
  public LocalDate end_schedule_date;

  /** 開始日. */
  @JsonFormat(pattern = "yyyy-MM-dd")
  @IQColumn
  public LocalDate start_date;

  /** 終了日. */
  @JsonFormat(pattern = "yyyy-MM-dd")
  @IQColumn
  public LocalDate end_date;

  /** 件名. */
  @IQColumn(length = 256, nullable = false)
  public String subject;

  /** 説明. */
  @IQColumn public String description;

  public Project() {}
}
