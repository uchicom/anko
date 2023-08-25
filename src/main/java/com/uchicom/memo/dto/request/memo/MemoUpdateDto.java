// (C) 2023 uchicom
package com.uchicom.memo.dto.request.memo;

import com.uchicom.memo.annotation.Form;
import jakarta.validation.constraints.NotNull;

@Form("memoUpdate")
public class MemoUpdateDto extends MemoRegisterDto {
  @NotNull(message = "必須です。")
  public Long memoId;
}
