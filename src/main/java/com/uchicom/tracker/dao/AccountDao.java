// (C) 2023 uchicom
package com.uchicom.tracker.dao;

import com.uchicom.tracker.dao.helper.DbHelper;
import com.uchicom.tracker.entity.Account;

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
