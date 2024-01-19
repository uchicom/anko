// (C) 2022 uchicom
package com.uchicom.memo.api;

import com.uchicom.memo.annotation.Path;
import com.uchicom.memo.dto.request.account.AccountRegisterDto;
import com.uchicom.memo.dto.request.account.LoginDto;
import com.uchicom.memo.dto.response.ErrorDto;
import com.uchicom.memo.dto.response.MessageDto;
import com.uchicom.memo.dto.response.account.TokenDto;
import com.uchicom.memo.service.AccountService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import javax.inject.Inject;

@Path("/account")
public class AccountApi extends AbstractApi {

  private final AccountService accountService;

  @Inject
  public AccountApi(AccountService accountService) {
    this.accountService = accountService;
  }

  @Path("/register")
  public Object register(AccountRegisterDto dto, HttpServletRequest req, HttpServletResponse res) {
    return trans(
        req,
        () -> {
          var account = accountService.register(dto);
          if (account == null) {
            return new ErrorDto("既に登録済みのログインIDです。");
          }
          return new MessageDto("アカウント登録しました。");
        });
  }

  @Path("/login")
  public Object login(LoginDto dto, HttpServletRequest req, HttpServletResponse res) {
    return trans(
        req,
        () -> {
          var token = accountService.login(dto);
          if (token == null) {
            return new ErrorDto("認証エラー");
          }
          return new TokenDto(token);
        });
  }
}
