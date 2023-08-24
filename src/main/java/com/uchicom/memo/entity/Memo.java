// (C) 2023 uchicom
package com.uchicom.memo.entity;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQTable;
import javax.inject.Inject;

/** メモ. */
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
  public String content;

  @Inject
  public Memo() {}
}
