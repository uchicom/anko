// (C) 2023 uchicom
package com.uchicom.tracker.dto.request.issue;

import com.uchicom.tracker.annotation.Form;
import jakarta.validation.constraints.NotNull;

@Form("issueUpdate")
public class IssueUpdateDto extends IssueRegisterDto {
  @NotNull(message = "必須です。")
  public Long issueId;
}
