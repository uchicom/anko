// (C) 2023 uchicom
package com.uchicom.memo.dao;

import com.uchicom.memo.entity.Memo;
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
    return helper.from(memo).where(memo.account_id).is(accountId).select();
  }
}
