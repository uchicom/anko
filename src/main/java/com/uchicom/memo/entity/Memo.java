// (C) 2023 uchicom
package com.uchicom.memo.entity;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;
import javax.inject.Inject;

/** メモ. */
@IQSchema("memo")
@IQTable(name = "memo", inheritColumns = true)
public class Memo extends AbstractTable {

  /** アカウントID. */
  @IQColumn(nullable = false)
  public Long account_id;

  /** タイトル. */
  @IQColumn(nullable = false)
  public String title;

  /** 本文. */
  @IQColumn(nullable = false)
  public String body;

  @Inject
  public Memo() {}
}
