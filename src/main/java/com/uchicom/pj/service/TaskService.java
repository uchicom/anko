// (C) 2025 uchicom
package com.uchicom.pj.service;

import com.uchicom.pj.dao.TaskDao;
import com.uchicom.pj.dto.request.task.TaskRegisterDto;
import com.uchicom.pj.dto.request.task.TaskUpdateDto;
import com.uchicom.pj.entity.Task;
import java.util.List;

public class TaskService {
  private final TaskDao taskDao;

  public TaskService(TaskDao taskDao) {
    this.taskDao = taskDao;
  }

  public List<Task> getList(long projectId) {
    return taskDao.findByProjectId(projectId);
  }

  public void register(TaskRegisterDto dto) {
    var task = new Task();
    task.project_id = dto.projectId;
    task.priority = dto.priority;
    task.cost = dto.cost;
    task.start_datetime = dto.startDatetime;
    task.subject = dto.subject;
    task.description = dto.description;
    task.progress = dto.progress;
    task.complete_datetime = dto.completeDatetime;
    taskDao.insert(task);
  }

  public void update(List<TaskUpdateDto> dtoList) {
    for (var dto : dtoList) {
      var task = taskDao.findById(dto.taskId);
      if (task == null) continue;
      task.project_id = dto.projectId;
      task.priority = dto.priority;
      task.cost = dto.cost;
      task.start_datetime = dto.startDatetime;
      task.subject = dto.subject;
      task.description = dto.description;
      task.progress = dto.progress;
      task.complete_datetime = dto.completeDatetime;
      taskDao.update(task);
    }
  }
}
