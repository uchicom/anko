// (C) 2025 uchicom
package com.uchicom.pj.dao;

import com.uchicom.pj.dao.helper.DbHelper;
import com.uchicom.pj.entity.Project;
import java.util.List;

public class ProjectDao extends AbstractDao<Project> {

  /** コンストラクタ. */
  public ProjectDao(DbHelper<Project> helper) {
    super(helper);
  }

  public List<Project> findByAccountId(long accountId) {
    var project = new Project();
    return helper.from(project).where(project.account_id).is(accountId).select();
  }

  public Project findByIdAndAccountId(long id, long accountId) {
    var project = new Project();
    return helper
        .from(project)
        .where(project.id)
        .is(id)
        .and(project.account_id)
        .is(accountId)
        .selectFirst();
  }
}
