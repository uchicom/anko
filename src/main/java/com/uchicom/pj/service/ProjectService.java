// (C) 2025 uchicom
package com.uchicom.pj.service;

import com.uchicom.pj.dao.ProjectDao;
import com.uchicom.pj.dto.request.project.ProjectRegisterDto;
import com.uchicom.pj.dto.request.project.ProjectUpdateDto;
import com.uchicom.pj.entity.Project;
import java.util.List;

public class ProjectService {
  private final ProjectDao projectDao;

  public ProjectService(ProjectDao projectDao) {
    this.projectDao = projectDao;
  }

  public Project get(long projectId) {
    return projectDao.findById(projectId);
  }

  public List<Project> getList(long accountId) {
    return projectDao.findByAccountId(accountId);
  }

  public void register(long accountId, ProjectRegisterDto dto) {
    var project = new Project();
    project.account_id = accountId;
    project.customer_id = dto.customerId;
    project.start_schedule_date = dto.startScheduleDate;
    project.end_schedule_date = dto.endScheduleDate;
    project.start_date = dto.startDate;
    project.end_date = dto.endDate;
    project.subject = dto.subject;
    project.description = dto.description;
    projectDao.insert(project);
  }

  public void update(long accountId, List<ProjectUpdateDto> dtoList) {
    for (var dto : dtoList) {
      var project = projectDao.findByIdAndAccountId(dto.projectId, accountId);
      if (project == null) continue;
      project.customer_id = dto.customerId;
      project.start_schedule_date = dto.startScheduleDate;
      project.end_schedule_date = dto.endScheduleDate;
      project.start_date = dto.startDate;
      project.end_date = dto.endDate;
      project.subject = dto.subject;
      project.description = dto.description;
      projectDao.update(project);
    }
  }
}
