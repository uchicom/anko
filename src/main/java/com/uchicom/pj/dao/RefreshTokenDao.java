// (C) 2026 uchicom
package com.uchicom.pj.dao;

import com.uchicom.pj.dao.helper.DbHelper;
import com.uchicom.pj.entity.RefreshToken;
import java.time.LocalDateTime;

/**
 * {@link RefreshToken}リフレッシュトークンリポジトリ.
 *
 * @author uchicom
 */
public class RefreshTokenDao extends AbstractDao<RefreshToken> {

  /** コンストラクタ. */
  public RefreshTokenDao(DbHelper<RefreshToken> helper) {
    super(helper);
  }

  public RefreshToken findByAccountId(Long accountId) {
    var refreshToken = new RefreshToken();
    return helper.from(refreshToken).where(refreshToken.account_id).is(accountId).selectFirst();
  }

  public RefreshToken findRefreshable(byte[] tokenHash, LocalDateTime now) {
    var refreshToken = new RefreshToken();
    return helper
        .from(refreshToken)
        .where(refreshToken.token_hash)
        .is(tokenHash)
        .and(refreshToken.expire_datetime)
        .atLeast(now)
        .and(refreshToken.inactive_datetime)
        .isNull()
        .selectFirst();
  }
}
