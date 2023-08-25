// (C) 2022 uchicom
package com.uchicom.memo.api;

import com.uchicom.memo.annotation.Auth;
import com.uchicom.memo.annotation.Path;
import com.uchicom.memo.dto.request.memo.MemoRegisterDto;
import com.uchicom.memo.dto.request.memo.MemoUpdateDto;
import com.uchicom.memo.dto.response.ListDto;
import com.uchicom.memo.dto.response.MessageDto;
import com.uchicom.memo.entity.Memo;
import com.uchicom.memo.service.AccountService;
import com.uchicom.memo.service.MemoService;
import java.util.List;
import javax.inject.Inject;
import spark.Request;
import spark.Response;

@Path("/memo")
public class MemoApi extends AbstractApi {

  private final AccountService accountService;
  private final MemoService memoService;

  @Inject
  public MemoApi(AccountService accountService, MemoService memoService) {
    this.accountService = accountService;
    this.memoService = memoService;
  }

  @Auth
  @Path("/list")
  public Object list(Request req, Response res) {
    return refer(
        () -> {
          return new ListDto<Memo>(memoService.getList(accountService.getAccountId(req)));
        });
  }

  @Auth
  @Path("/register")
  public Object register(MemoRegisterDto dto, Request req, Response res) {
    return trans(
        req,
        () -> {
          memoService.register(accountService.getAccountId(req), dto);
          return new MessageDto("メモを登録しました。");
        });
  }

  @Auth
  @Path("/update")
  public Object update(List<MemoUpdateDto> dtoList, Request req, Response res) {
    return trans(
        req,
        () -> {
          memoService.update(accountService.getAccountId(req), dtoList);
          return new MessageDto("メモを更新しました。");
        });
  }
}
