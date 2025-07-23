// (C) 2023 uchicom
package com.uchicom.tracker.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import com.uchicom.tracker.AbstractTest;
import com.uchicom.tracker.dao.IssueDao;
import com.uchicom.tracker.dto.request.issue.IssueRegisterDto;
import com.uchicom.tracker.dto.request.issue.IssueUpdateDto;
import com.uchicom.tracker.entity.Issue;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

/**
 * {@link IssueService}のテストケース.
 *
 * @author uchicom
 */
@Tag("service")
public class IssueServiceTest extends AbstractTest {

  @Mock IssueDao issueDao;

  @Captor ArgumentCaptor<String> loginIdCaptor;
  @Captor ArgumentCaptor<String> ipCaptor;
  @Captor ArgumentCaptor<Issue> issueCaptor;
  @Captor ArgumentCaptor<Long> accountIdCaptor;
  @Captor ArgumentCaptor<Long> issueIdCaptor;

  @Spy @InjectMocks IssueService service;

  @Test
  public void get() {
    var issue = new Issue();
    doReturn(issue).when(issueDao).findById(issueIdCaptor.capture());
    var issueId = 1L;

    // test
    var result = service.get(issueId);

    // assert
    assertThat(result).isEqualTo(issue);
    assertThat(issueIdCaptor.getValue()).isEqualTo(issueId);
  }

  @Test
  public void getList() {
    var list = List.of(new Issue());
    doReturn(list).when(issueDao).findByAccountId(accountIdCaptor.capture());

    // test
    var result = service.getList(1L);

    // assert
    assertThat(result).isEqualTo(list);
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
  }

  @Test
  public void register() {
    doReturn(true).when(issueDao).insert(issueCaptor.capture());
    var dto = new IssueRegisterDto();
    dto.subject = "subject";
    dto.detail = "detail";

    // test
    service.register(1L, dto);

    // assert
    var issue = issueCaptor.getValue();
    assertThat(issue.account_id).isEqualTo(1L);
    assertThat(issue.subject).isEqualTo(dto.subject);
    assertThat(issue.detail).isEqualTo(dto.detail);
  }

  @Test
  public void update() {
    var issue2 = new Issue();
    issue2.subject = "subject2";
    issue2.detail = "detail2";
    var issue3 = new Issue();
    issue3.subject = "subject3";
    issue3.detail = "detail3";
    doReturn(null)
        .doReturn(issue2)
        .doReturn(issue3)
        .when(issueDao)
        .findByIdAndAccountId(issueIdCaptor.capture(), accountIdCaptor.capture());
    doReturn(true).when(issueDao).update(issueCaptor.capture());

    var dto1 = new IssueUpdateDto();
    dto1.issueId = 1L;
    dto1.subject = "subject1";
    dto1.detail = "detail1";

    var dto2 = new IssueUpdateDto();
    dto2.issueId = 2L;
    dto2.subject = "subject2";
    dto2.detail = "detail2";

    var dto3 = new IssueUpdateDto();
    dto3.issueId = 3L;
    dto3.subject = "subject31";
    dto3.detail = "detail31";

    // test
    service.update(4L, List.of(dto1, dto2, dto3));

    // assert
    var issueIds = issueIdCaptor.getAllValues();
    assertThat(issueIds).hasSize(3);
    assertThat(issueIds.get(0)).isEqualTo(dto1.issueId);
    assertThat(issueIds.get(1)).isEqualTo(dto2.issueId);
    assertThat(issueIds.get(2)).isEqualTo(dto3.issueId);

    var accountIds = accountIdCaptor.getAllValues();
    assertThat(accountIds).hasSize(3);
    assertThat(accountIds.get(0)).isEqualTo(4L);
    assertThat(accountIds.get(1)).isEqualTo(4L);
    assertThat(accountIds.get(2)).isEqualTo(4L);

    assertThat(issueCaptor.getValue()).isEqualTo(issue3);
    assertThat(issue3.subject).isEqualTo("subject31");
    assertThat(issue3.detail).isEqualTo("detail31");
  }
}
