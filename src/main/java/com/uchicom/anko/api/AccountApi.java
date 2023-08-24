// (C) 2022 Rigger LLC
package com.uchicom.anko.api;

import javax.inject.Inject;
import com.uchicom.anko.annotation.Path;
import com.uchicom.anko.dto.request.account.LoginDto;
import com.uchicom.anko.dto.request.account.RegisterDto;
import com.uchicom.anko.dto.response.ErrorDto;
import com.uchicom.anko.dto.response.MessageDto;
import com.uchicom.anko.dto.response.account.TokenDto;
import com.uchicom.anko.service.AccountService;
import spark.Request;
import spark.Response;

@Path("/account")
public class AccountApi extends AbstractApi{

  private final AccountService accountService;

  @Inject
  public AccountApi(AccountService accountService) {
    this.accountService = accountService;
  }

  @Path("/register")
  public Object register(RegisterDto dto, Request req, Response res) {
    return trans(
        req,
        () -> {
          var account = accountService.register(dto.id, dto.pass, dto.name);
          if (account == null) {
            return new ErrorDto("既に登録済みのログインIDです。");
          }
          return new MessageDto("アカウント登録しました。");
        });
  }

  @Path("/login")
  public Object login(LoginDto dto, Request req, Response res) {
    return trans(
        req,
        () -> {
          var token = accountService.login(dto.id, dto.pass);
          if (token == null) {
            return new ErrorDto("認証エラー");
          }
          return new TokenDto(token);
        });
  }
}