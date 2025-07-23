// (C) 2023 uchicom
package com.uchicom.tracker.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import com.uchicom.tracker.AbstractTest;
import com.uchicom.tracker.dto.request.issue.IssueIdentificationDto;
import com.uchicom.tracker.dto.request.issue.IssueRegisterDto;
import com.uchicom.tracker.dto.request.issue.IssueUpdateDto;
import com.uchicom.tracker.dto.response.ListDto;
import com.uchicom.tracker.dto.response.MessageDto;
import com.uchicom.tracker.entity.Issue;
import com.uchicom.tracker.service.AccountService;
import com.uchicom.tracker.service.IssueService;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

@Tag("api")
public class IssueApiTest extends AbstractTest {
  @Mock HttpServletRequest req;
  @Mock HttpServletResponse res;

  @Mock AccountService accountService;
  @Mock IssueService issueService;

  @Captor ArgumentCaptor<Long> accountIdCaptor;
  @Captor ArgumentCaptor<IssueRegisterDto> issueRegisterDtoCaptor;
  @Captor ArgumentCaptor<List<IssueUpdateDto>> issueUpdateDtoListCaptor;
  @Captor ArgumentCaptor<Long> issueIdCaptor;

  @Spy @InjectMocks IssueApi api;

  @BeforeEach
  public void setUp() {
    createApiMock(api);
  }

  @Test
  public void get() throws Exception {
    // mock
    var issue = new Issue();
    doReturn(issue).when(issueService).get(issueIdCaptor.capture());
    var dto = new IssueIdentificationDto();
    dto.issueId = 1L;

    // test
    var result = api.get(dto, req, res);

    // assert
    assertThat(result).isEqualTo(issue);
    assertThat(issueIdCaptor.getValue()).isEqualTo(dto.issueId);
  }

  @Test
  public void list() throws Exception {
    // mock
    doReturn(1L).when(api).getAccountId(req);
    var list = List.of(new Issue());
    doReturn(list).when(issueService).getList(accountIdCaptor.capture());

    // test
    var result = api.list(req, res);

    // assert
    if (result instanceof ListDto<?> listDto) {
      assertThat(listDto.list).isEqualTo(list);
    } else {
      fail();
    }
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
  }

  @Test
  public void register() throws Exception {
    // mock
    doReturn(1L).when(api).getAccountId(req);
    var dto = new IssueRegisterDto();
    doNothing()
        .when(issueService)
        .register(accountIdCaptor.capture(), issueRegisterDtoCaptor.capture());

    // test
    var result = api.register(dto, req, res);

    // assert
    if (result instanceof MessageDto messageDto) {
      assertThat(messageDto.message).isEqualTo("メモを登録しました。");
    } else {
      fail();
    }
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
    assertThat(issueRegisterDtoCaptor.getValue()).isEqualTo(dto);
  }

  @Test
  public void update() throws Exception {
    // mock
    doReturn(1L).when(api).getAccountId(req);
    doNothing()
        .when(issueService)
        .update(accountIdCaptor.capture(), issueUpdateDtoListCaptor.capture());

    var list = List.of(new IssueUpdateDto());
    // test
    var result = api.update(list, req, res);

    // assert
    if (result instanceof MessageDto messageDto) {
      assertThat(messageDto.message).isEqualTo("メモを更新しました。");
    } else {
      fail();
    }
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
    assertThat(issueUpdateDtoListCaptor.getValue()).isEqualTo(list);
  }
}
