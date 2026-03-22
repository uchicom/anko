// (C) 2025 uchicom
package com.uchicom.pj.entity;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;

/** タスクとアカウントの紐付け. */
@IQSchema("pj")
@IQTable(name = "task_to_account", inheritColumns = true)
public class TaskToAccount extends AbstractTable {

  /** タスクID. */
  @IQColumn(nullable = false)
  public Long task_id;

  /** アカウントID. */
  @IQColumn(nullable = false)
  public Long account_id;
}
