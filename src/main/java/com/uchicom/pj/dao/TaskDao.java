// (C) 2025 uchicom
package com.uchicom.pj.dao;

import com.uchicom.pj.dao.helper.DbHelper;
import com.uchicom.pj.entity.Task;
import java.util.List;

public class TaskDao extends AbstractDao<Task> {

  /** コンストラクタ. */
  public TaskDao(DbHelper<Task> helper) {
    super(helper);
  }

  public List<Task> findByProjectId(long projectId) {
    var task = new Task();
    return helper.from(task).where(task.project_id).is(projectId).select();
  }
}
