// (C) 2023 uchicom
package com.uchicom.tracker.dto.request.account;

import com.uchicom.tracker.annotation.Form;
import jakarta.validation.constraints.NotBlank;

@Form("accountRegister")
public class AccountRegisterDto {
  @NotBlank(message = "必須です。")
  public String id;

  @NotBlank(message = "必須です。")
  public String pass;

  @NotBlank(message = "必須です。")
  public String name;
}
