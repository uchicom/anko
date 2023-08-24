// (C) 2023 uchicom
package com.uchicom.memo.service;

import com.uchicom.memo.dao.MemoDao;
import com.uchicom.memo.entity.Memo;
import java.util.List;
import javax.inject.Inject;

public class MemoService {
  private final MemoDao memoDao;

  @Inject
  public MemoService(MemoDao memoDao) {
    this.memoDao = memoDao;
  }
  // 一覧 getList

  public List<Memo> getList(long accountId) {
    return memoDao.findByAccountId(accountId);
  }

  // 登録 register
  // 変更（登録and削除） save(update and delete)

}
