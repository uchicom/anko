// (C) 2023 uchicom
package com.uchicom.memo.dto.request.memo;

import com.uchicom.memo.annotation.Form;
import jakarta.validation.constraints.NotBlank;

@Form("memoRegister")
public class MemoRegisterDto {
  @NotBlank(message = "必須です。")
  public String title;

  @NotBlank(message = "必須です。")
  public String body;
}
