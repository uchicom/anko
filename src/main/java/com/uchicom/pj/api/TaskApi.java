// (C) 2025 uchicom
package com.uchicom.pj.api;

import com.uchicom.pj.annotation.Auth;
import com.uchicom.pj.annotation.Path;
import com.uchicom.pj.dto.request.task.TaskListDto;
import com.uchicom.pj.dto.request.task.TaskRegisterDto;
import com.uchicom.pj.dto.request.task.TaskUpdateDto;
import com.uchicom.pj.dto.response.ListDto;
import com.uchicom.pj.dto.response.MessageDto;
import com.uchicom.pj.entity.Task;
import com.uchicom.pj.service.TaskService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Path("/task")
public class TaskApi extends AbstractApi {

  private final TaskService taskService;

  public TaskApi(TaskService taskService) {
    this.taskService = taskService;
  }

  @Auth
  @Path("/list")
  public Object list(TaskListDto dto, HttpServletRequest req, HttpServletResponse res) {
    return refer(
        () -> {
          return new ListDto<Task>(taskService.getList(dto.projectId));
        });
  }

  @Auth
  @Path("/register")
  public Object register(TaskRegisterDto dto, HttpServletRequest req, HttpServletResponse res) {
    return trans(
        req,
        () -> {
          taskService.register(dto);
          return new MessageDto("タスクを登録しました。");
        });
  }

  @Auth
  @Path("/update")
  public Object update(
      List<TaskUpdateDto> dtoList, HttpServletRequest req, HttpServletResponse res) {
    return trans(
        req,
        () -> {
          taskService.update(dtoList);
          return new MessageDto("タスクを更新しました。");
        });
  }
}
