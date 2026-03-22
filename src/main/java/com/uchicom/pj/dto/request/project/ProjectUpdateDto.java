// (C) 2025 uchicom
package com.uchicom.pj.dto.request.project;

import com.uchicom.pj.annotation.Form;
import jakarta.validation.constraints.NotNull;

@Form("projectUpdate")
public class ProjectUpdateDto extends ProjectRegisterDto {
  @NotNull(message = "必須です。")
  public Long projectId;
}
