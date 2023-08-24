package com.uchicom.anko.dto.request.account;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.validation.constraints.NotBlank;
import com.uchicom.anko.annotation.Form;

@Form("login")
@JsonIgnoreProperties(ignoreUnknown = true)
public class LoginDto {
  @NotBlank(message = "必須です。")
  public String id;

  @NotBlank(message = "必須です。")
  public String pass;
}
