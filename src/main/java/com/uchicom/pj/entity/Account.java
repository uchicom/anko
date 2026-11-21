// (C) 2023 uchicom
package com.uchicom.pj.entity;

import com.uchicom.orz.Iciql.IQColumn;
import com.uchicom.orz.Iciql.IQSchema;
import com.uchicom.orz.Iciql.IQTable;

/** アカウント. */
@IQSchema("pj")
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
}
