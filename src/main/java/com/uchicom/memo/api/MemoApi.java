// (C) 2022 uchicom
package com.uchicom.memo.api;

import com.uchicom.memo.annotation.Auth;
import com.uchicom.memo.annotation.Path;
import com.uchicom.memo.dto.request.memo.MemoRegisterDto;
import com.uchicom.memo.dto.request.memo.MemoUpdateDto;
import com.uchicom.memo.dto.response.ListDto;
import com.uchicom.memo.dto.response.MessageDto;
import com.uchicom.memo.entity.Memo;
import com.uchicom.memo.service.MemoService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import javax.inject.Inject;

@Path("/memo")
public class MemoApi extends AbstractApi {

  private final MemoService memoService;

  @Inject
  public MemoApi(MemoService memoService) {
    this.memoService = memoService;
  }

  @Auth
  @Path("/list")
  public Object list(HttpServletRequest req, HttpServletResponse res) {
    return refer(
        () -> {
          return new ListDto<Memo>(memoService.getList(getAccountId(req)));
        });
  }

  @Auth
  @Path("/register")
  public Object register(MemoRegisterDto dto, HttpServletRequest req, HttpServletResponse res) {
    return trans(
        req,
        () -> {
          memoService.register(getAccountId(req), dto);
          return new MessageDto("メモを登録しました。");
        });
  }

  @Auth
  @Path("/update")
  public Object update(
      List<MemoUpdateDto> dtoList, HttpServletRequest req, HttpServletResponse res) {
    return trans(
        req,
        () -> {
          memoService.update(getAccountId(req), dtoList);
          return new MessageDto("メモを更新しました。");
        });
  }
}
