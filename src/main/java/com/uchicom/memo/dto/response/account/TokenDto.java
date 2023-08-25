// (C) 2023 uchicom
package com.uchicom.memo.dto.response.account;

public class TokenDto {
  public String token;
  public String message;

  public TokenDto(String token) {
    this.token = token;
  }

  public TokenDto(String token, String message) {
    this(token);
    this.message = message;
  }
}
