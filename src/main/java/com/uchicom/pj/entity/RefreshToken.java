// (C) 2026 uchicom
package com.uchicom.pj.entity;

import com.iciql.Iciql.IQColumn;
import com.iciql.Iciql.IQContraintUnique;
import com.iciql.Iciql.IQSchema;
import com.iciql.Iciql.IQTable;
import java.time.LocalDateTime;

/** リフレッシュトークン. */
@IQContraintUnique(uniqueColumns = {"account_id"})
@IQSchema("pj")
@IQTable(name = "refresh_token", inheritColumns = true)
public class RefreshToken extends AbstractTable {

  /** アカウントID. */
  @IQColumn(nullable = false)
  public Long account_id;

  /** トークンハッシュ. */
  @IQColumn(length = 128, nullable = false)
  public byte[] token_hash;

  /** 有効期限日時. */
  @IQColumn(nullable = false)
  public LocalDateTime expire_datetime;

  /** 無効日時. */
  @IQColumn public LocalDateTime inactive_datetime;
}
