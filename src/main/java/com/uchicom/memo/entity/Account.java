// (C) 2023 uchicom
package com.uchicom.memo.entity;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;
import javax.inject.Inject;

/** アカウント. */
@IQSchema("memo")
@IQTable(name = "account", inheritColumns = true)
public class Account extends AbstractTable {

  /** ログインID. */
  @IQColumn(length = 128, nullable = false)
  public String login_id;

  /** パスワードハッシュ. */
  @IQColumn(length = 128, nullable = false)
  public byte[] password;

  /** 名前. */
  @IQColumn(length = 128, nullable = false)
  public String name;

  @Inject
  public Account() {}
}
