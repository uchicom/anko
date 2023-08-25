// (C) 2023 uchicom
package com.uchicom.memo.dto.request.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.uchicom.memo.annotation.Form;
import jakarta.validation.constraints.NotBlank;

@Form("login")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginDto {
  @NotBlank(message = "必須です。")
  public String id;

  @NotBlank(message = "必須です。")
  public String pass;
}
