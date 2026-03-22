// (C) 2025 uchicom
package com.uchicom.pj.dto.request.task;

import com.uchicom.pj.annotation.Form;
import jakarta.validation.constraints.NotNull;

@Form("taskUpdate")
public class TaskUpdateDto extends TaskRegisterDto {
  @NotNull(message = "必須です。")
  public Long taskId;
}
