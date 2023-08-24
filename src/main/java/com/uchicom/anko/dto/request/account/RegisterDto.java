package com.uchicom.anko.dto.request.account;

import com.uchicom.anko.annotation.Form;

import jakarta.validation.constraints.NotBlank;

@Form("accountRegister")
public class RegisterDto {
  @NotBlank(message = "必須です。")
  public String id;

  @NotBlank(message = "必須です。")
  public String pass;

  @NotBlank(message = "必須です。")
  public String name;
}
