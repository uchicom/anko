// (C) 2025 uchicom
package com.uchicom.pj.entity;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;
import java.time.LocalDateTime;

/** タスク. */
@IQSchema("pj")
@IQTable(name = "task", inheritColumns = true)
public class Task extends AbstractTable {

  /** プロジェクトID. */
  @IQColumn(nullable = false)
  public Long project_id;

  /** 優先度. */
  @IQColumn public Integer priority;

  /** コスト. */
  @IQColumn public Double cost;

  /** 開始日時. */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @IQColumn
  public LocalDateTime start_datetime;

  /** 件名. */
  @IQColumn(length = 256, nullable = false)
  public String subject;

  /** 説明. */
  @IQColumn public String description;

  /** 進捗. */
  @IQColumn public Integer progress;

  /** 完了日時. */
  @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
  @IQColumn
  public LocalDateTime complete_datetime;
}
