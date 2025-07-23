// (C) 2023 uchicom
package com.uchicom.tracker.dto.request.issue;

import com.uchicom.tracker.annotation.Form;
import jakarta.validation.constraints.NotBlank;

@Form("issueRegister")
public class IssueRegisterDto {
  @NotBlank(message = "必須です。")
  public String subject;

  @NotBlank(message = "必須です。")
  public String detail;
}
