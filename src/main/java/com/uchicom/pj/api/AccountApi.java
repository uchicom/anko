// (C) 2022 uchicom
package com.uchicom.pj.api;

import com.uchicom.pj.annotation.Path;
import com.uchicom.pj.dto.request.account.AccountRegisterDto;
import com.uchicom.pj.dto.request.account.LoginDto;
import com.uchicom.pj.dto.response.ErrorDto;
import com.uchicom.pj.dto.response.MessageDto;
import com.uchicom.pj.dto.response.account.ResultDto;
import com.uchicom.pj.enumeration.ApiResult;
import com.uchicom.pj.service.AccountService;
import com.uchicom.pj.service.CookieService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Path("/account")
public class AccountApi extends AbstractApi {

  private final AccountService accountService;
  private final CookieService cookieService;

  public AccountApi(AccountService accountService, CookieService cookieService) {
    this.accountService = accountService;
    this.cookieService = cookieService;
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
          cookieService.addJwt(res, token);
          return new ResultDto(ApiResult.OK);
        });
  }

  @Path("/check/login")
  public Object checkLogin(HttpServletRequest req, HttpServletResponse res) {
    return refer(
        () -> {
          if (accountService.isLogin(req)) {
            return new ResultDto(ApiResult.OK);
          }
          return new ResultDto(ApiResult.NG);
        });
  }

  @Path("/logout")
  public Object logout(HttpServletRequest req, HttpServletResponse res) {
    return refer(
        () -> {
          cookieService.removeJwt(req, res);
          return new ResultDto(ApiResult.OK);
        });
  }
}
