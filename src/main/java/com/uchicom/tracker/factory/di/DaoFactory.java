// (C) 2025 uchicom
package com.uchicom.tracker.factory.di;

import com.uchicom.tracker.dao.AccountDao;
import com.uchicom.tracker.dao.IssueDao;
import com.uchicom.tracker.dao.helper.DbHelper;
import com.uchicom.tracker.entity.AbstractTable;
import com.uchicom.tracker.entity.Account;
import com.uchicom.tracker.entity.Issue;

public class DaoFactory {

  static <T extends AbstractTable> DbHelper<T> helper(T entity) {
    return new DbHelper<>(entity);
  }

  static AccountDao accountDao() {
    return new AccountDao(helper(new Account()));
  }

  static IssueDao issueDao() {
    return new IssueDao(helper(new Issue()));
  }
}
