// (C) 2023 uchicom
package com.uchicom.pj.dao;

import com.uchicom.pj.dao.helper.DbHelper;
import com.uchicom.pj.entity.Account;

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
