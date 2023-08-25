// (C) 2023 uchicom
package com.uchicom.memo.service;

import com.uchicom.memo.dao.MemoDao;
import com.uchicom.memo.dto.request.memo.MemoRegisterDto;
import com.uchicom.memo.dto.request.memo.MemoUpdateDto;
import com.uchicom.memo.entity.Memo;
import java.util.List;
import javax.inject.Inject;

public class MemoService {
  private final MemoDao memoDao;

  @Inject
  public MemoService(MemoDao memoDao) {
    this.memoDao = memoDao;
  }

  public List<Memo> getList(long accountId) {
    return memoDao.findByAccountId(accountId);
  }

  public void register(long accountId, MemoRegisterDto dto) {
    var memo = new Memo();
    memo.account_id = accountId;
    memo.title = dto.title;
    memo.body = dto.body;
    memoDao.insert(memo);
  }

  public void update(long accountId, List<MemoUpdateDto> dtoList) {
    for (var dto : dtoList) {
      var memo = memoDao.findByIdAndAccountId(dto.memoId, accountId);
      if (memo == null) continue;
      if (memo.title.equals(dto.title) && memo.body.equals(dto.body)) {
        continue;
      }
      memo.title = dto.title;
      memo.body = dto.body;
      memoDao.update(memo);
    }
  }
}
