// (C) 2023 uchicom
package com.uchicom.memo.dao;

import com.uchicom.memo.dao.helper.DbHelper;
import com.uchicom.memo.entity.Account;

public class AccountDao extends AbstractDao<Account> {

  /** コンストラクタ. */
  public AccountDao(DbHelper<Account> helper) {
    super(helper);
  }

  public Account findByLoginId(String loginId) {
    var account = new Account();
    return helper.from(account).where(account.login_id).is(loginId).selectFirst();
  }
}
