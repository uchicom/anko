// (C) 2023 uchicom
package com.uchicom.tracker.entity;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;

/** 問題. */
@IQSchema("tracker")
@IQTable(name = "issue", inheritColumns = true)
public class Issue extends AbstractTable {

  /** アカウントID. */
  @IQColumn(nullable = false)
  public Long account_id;

  /** 件名. */
  @IQColumn(nullable = false)
  public String subject;

  /** 詳細. */
  @IQColumn(nullable = false)
  public String detail;

  public Issue() {}
}
