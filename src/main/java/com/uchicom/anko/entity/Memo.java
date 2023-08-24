package com.uchicom.anko.entity;

  
import javax.inject.Inject;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;

/** メモ. */
@IQSchema("anko")
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
