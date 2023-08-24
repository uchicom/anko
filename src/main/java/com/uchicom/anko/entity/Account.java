package com.uchicom.anko.entity;

import javax.inject.Inject;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;

/** アカウント. */
@IQSchema("anko")
@IQTable(name = "account", inheritColumns = true)
public class Account extends AbstractTable {

  /** ログインID. */
  @IQColumn(length = 255, nullable = false)
  public String login_id;

  /** パスワードハッシュ. */
  @IQColumn(length = 128, nullable = false)
  public byte[] password;

  /** 名前. */
  @IQColumn(length = 255, nullable = false)
  public String name;

  @Inject
  public Account() {}
}
