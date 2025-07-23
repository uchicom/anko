// (C) 2023 uchicom
package com.uchicom.tracker.service;

import com.uchicom.tracker.dao.IssueDao;
import com.uchicom.tracker.dto.request.issue.IssueRegisterDto;
import com.uchicom.tracker.dto.request.issue.IssueUpdateDto;
import com.uchicom.tracker.entity.Issue;
import java.util.List;

public class IssueService {
  private final IssueDao issueDao;

  public IssueService(IssueDao issueDao) {
    this.issueDao = issueDao;
  }

  public Issue get(long issueId) {
    return issueDao.findById(issueId);
  }

  public List<Issue> getList(long accountId) {
    return issueDao.findByAccountId(accountId);
  }

  public void register(long accountId, IssueRegisterDto dto) {
    var issue = new Issue();
    issue.account_id = accountId;
    issue.subject = dto.subject;
    issue.detail = dto.detail;
    issueDao.insert(issue);
  }

  public void update(long accountId, List<IssueUpdateDto> dtoList) {
    for (var dto : dtoList) {
      var issue = issueDao.findByIdAndAccountId(dto.issueId, accountId);
      if (issue == null) continue;
      if (issue.subject.equals(dto.subject) && issue.detail.equals(dto.detail)) {
        continue;
      }
      issue.subject = dto.subject;
      issue.detail = dto.detail;
      issueDao.update(issue);
    }
  }
}
