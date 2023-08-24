package com.uchicom.anko.dao;


import com.iciql.NestedConditions.Or;
import com.uchicom.anko.entity.Account;

import java.time.LocalDate;
import java.util.List;
import javax.inject.Inject;

public class AccountDao extends AbstractDao<Account> {

  /** コンストラクタ. */
  @Inject
  public AccountDao(DbHelper<Account> helper) {
    super(helper);
  }

  public Account findByLoginId(String loginId) {
    var account = new Account();
    return helper
        .from(account)
        .where(account.login_id)
        .is(loginId)
        .selectFirst();
  }
}
