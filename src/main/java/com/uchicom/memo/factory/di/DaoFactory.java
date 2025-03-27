// (C) 2025 uchicom
package com.uchicom.memo.factory.di;

import com.uchicom.memo.dao.AccountDao;
import com.uchicom.memo.dao.MemoDao;
import com.uchicom.memo.dao.helper.DbHelper;
import com.uchicom.memo.entity.AbstractTable;
import com.uchicom.memo.entity.Account;
import com.uchicom.memo.entity.Memo;

public class DaoFactory {

  static <T extends AbstractTable> DbHelper<T> helper(T entity) {
    return new DbHelper<>(entity);
  }

  static AccountDao accountDao() {
    return new AccountDao(helper(new Account()));
  }

  static MemoDao memoDao() {
    return new MemoDao(helper(new Memo()));
  }
}
