package com.uchicom.anko.dao;

  
import com.iciql.NestedConditions.Or;
import com.uchicom.anko.entity.Account;
import com.uchicom.anko.entity.Memo;

import java.util.List;
import javax.inject.Inject;

public class MemoDao extends AbstractDao<Memo> {

  /** コンストラクタ. */
  @Inject
  public MemoDao(DbHelper<Memo> helper) {
    super(helper);
  }

  public List<Memo> findByAccountId(long accountId) {
    var memo = new Memo();
    return helper
        .from(memo)
        .where(memo.account_id)
        .is(accountId)
        .select();
  }
}
