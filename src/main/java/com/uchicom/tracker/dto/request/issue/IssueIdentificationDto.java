// (C) 2024 uchicom
package com.uchicom.tracker.dto.request.issue;

import com.uchicom.tracker.annotation.Form;
import jakarta.validation.constraints.NotNull;

@Form("issueIdentification")
public class IssueIdentificationDto {
  @NotNull(message = "必須です。")
  public Long issueId;
}
