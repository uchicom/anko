// (C) 2025 uchicom
package com.uchicom.pj.dto.request.task;

import com.uchicom.pj.annotation.Form;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.LocalDateTime;

@Form("taskRegister")
public class TaskRegisterDto {
  @NotNull(message = "必須です。")
  public Long projectId;

  public Integer priority;

  public Double cost;

  public LocalDateTime startDatetime;

  @NotBlank(message = "必須です。")
  public String subject;

  public String description;

  public Integer progress;

  public LocalDateTime completeDatetime;
}
