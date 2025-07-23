// (C) 2022 uchicom
package com.uchicom.tracker.api;

import com.uchicom.tracker.annotation.Auth;
import com.uchicom.tracker.annotation.Path;
import com.uchicom.tracker.dto.request.issue.IssueIdentificationDto;
import com.uchicom.tracker.dto.request.issue.IssueRegisterDto;
import com.uchicom.tracker.dto.request.issue.IssueUpdateDto;
import com.uchicom.tracker.dto.response.ListDto;
import com.uchicom.tracker.dto.response.MessageDto;
import com.uchicom.tracker.entity.Issue;
import com.uchicom.tracker.service.IssueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;

@Path("/issue")
public class IssueApi extends AbstractApi {

  private final IssueService issueService;

  public IssueApi(IssueService issueService) {
    this.issueService = issueService;
  }

  @Auth
  @Path("/get")
  public Object get(IssueIdentificationDto dto, HttpServletRequest req, HttpServletResponse res) {
    return refer(
        () -> {
          return issueService.get(dto.issueId);
        });
  }

  @Auth
  @Path("/list")
  public Object list(HttpServletRequest req, HttpServletResponse res) {
    return refer(
        () -> {
          return new ListDto<Issue>(issueService.getList(getAccountId(req)));
        });
  }

  @Auth
  @Path("/register")
  public Object register(IssueRegisterDto dto, HttpServletRequest req, HttpServletResponse res) {
    return trans(
        req,
        () -> {
          issueService.register(getAccountId(req), dto);
          return new MessageDto("メモを登録しました。");
        });
  }

  @Auth
  @Path("/update")
  public Object update(
      List<IssueUpdateDto> dtoList, HttpServletRequest req, HttpServletResponse res) {
    return trans(
        req,
        () -> {
          issueService.update(getAccountId(req), dtoList);
          return new MessageDto("メモを更新しました。");
        });
  }
}
