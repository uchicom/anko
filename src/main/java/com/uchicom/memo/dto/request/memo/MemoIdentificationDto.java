// (C) 2024 uchicom
package com.uchicom.memo.dto.request.memo;

import com.uchicom.memo.annotation.Form;
import jakarta.validation.constraints.NotNull;

@Form("memoIdentification")
public class MemoIdentificationDto {
  @NotNull(message = "必須です。")
  public Long memoId;
}
