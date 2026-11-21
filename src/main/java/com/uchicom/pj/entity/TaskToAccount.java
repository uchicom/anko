// (C) 2025 uchicom
package com.uchicom.pj.entity;

import com.uchicom.orz.Iciql.IQColumn;
import com.uchicom.orz.Iciql.IQSchema;
import com.uchicom.orz.Iciql.IQTable;

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
