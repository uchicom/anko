// (C) 2022 Rigger LLC
package com.uchicom.anko.api;

import javax.inject.Inject;
import com.uchicom.anko.annotation.Path;
import com.uchicom.anko.dto.request.account.LoginDto;
import com.uchicom.anko.dto.response.ErrorDto;
import com.uchicom.anko.dto.response.account.TokenDto;
import com.uchicom.anko.service.AccountService;
import com.uchicom.anko.service.MemoService;

import spark.Request;
import spark.Response;

@Path("/memo")
public class MemoApi extends AbstractApi{

  private final AccountService accountService;
  private final MemoService memoService;

  @Inject
  public MemoApi(AccountService accountService, MemoService memoService) {
    this.accountService = accountService;
    this.memoService = memoService;
  }
  // register 登録　id pass を登録して保存 insertのみ存在してたらエラー

  @Path("/list")
  public Object list(Request req, Response res) {
    return refer(
        () -> {
          return memoService.getList(accountService.getAccountId(req));
        });
  }
  @Path("/register")
  public Object register(LoginDto dto, Request req, Response res) {
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
  @Path("/update")
  public Object update(LoginDto dto, Request req, Response res) {
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