// (C) 2023 uchicom
package com.uchicom.memo.dao;

import com.uchicom.memo.dao.helper.DbHelper;
import com.uchicom.memo.entity.Memo;
import java.util.List;

public class MemoDao extends AbstractDao<Memo> {

  /** コンストラクタ. */
  public MemoDao(DbHelper<Memo> helper) {
    super(helper);
  }

  public List<Memo> findByAccountId(long accountId) {
    var memo = new Memo();
    return helper.from(memo).where(memo.account_id).is(accountId).select();
  }

  public Memo findByIdAndAccountId(long id, long accountId) {
    var memo = new Memo();
    return helper.from(memo).where(memo.id).is(id).and(memo.account_id).is(accountId).selectFirst();
  }
}
