// (C) 2023 uchicom
package com.uchicom.tracker.dao;

import com.uchicom.tracker.dao.helper.DbHelper;
import com.uchicom.tracker.entity.Issue;
import java.util.List;

public class IssueDao extends AbstractDao<Issue> {

  /** コンストラクタ. */
  public IssueDao(DbHelper<Issue> helper) {
    super(helper);
  }

  public List<Issue> findByAccountId(long accountId) {
    var issue = new Issue();
    return helper.from(issue).where(issue.account_id).is(accountId).select();
  }

  public Issue findByIdAndAccountId(long id, long accountId) {
    var issue = new Issue();
    return helper
        .from(issue)
        .where(issue.id)
        .is(id)
        .and(issue.account_id)
        .is(accountId)
        .selectFirst();
  }
}
