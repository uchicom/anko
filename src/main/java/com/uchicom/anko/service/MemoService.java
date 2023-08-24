package com.uchicom.anko.service;

import java.util.List;

import javax.inject.Inject;

import com.uchicom.anko.dao.MemoDao;
import com.uchicom.anko.entity.Memo;

public class MemoService {
  private final MemoDao memoDao;
  @Inject
  public MemoService(MemoDao memoDao){
    this.memoDao = memoDao;
  }
  // 一覧 getList

  public List<Memo> getList(long accountId) {
    return memoDao.findByAccountId(accountId);
  }
  
  // 登録 register
  // 変更（登録and削除） save(update and delete)

}
