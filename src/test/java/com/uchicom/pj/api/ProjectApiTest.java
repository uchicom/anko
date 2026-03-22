// (C) 2025 uchicom
package com.uchicom.pj.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import com.uchicom.pj.AbstractTest;
import com.uchicom.pj.dto.request.project.ProjectIdentificationDto;
import com.uchicom.pj.dto.request.project.ProjectRegisterDto;
import com.uchicom.pj.dto.request.project.ProjectUpdateDto;
import com.uchicom.pj.dto.response.ListDto;
import com.uchicom.pj.dto.response.MessageDto;
import com.uchicom.pj.entity.Project;
import com.uchicom.pj.service.ProjectService;
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
public class ProjectApiTest extends AbstractTest {
  @Mock HttpServletRequest req;
  @Mock HttpServletResponse res;

  @Mock ProjectService projectService;

  @Captor ArgumentCaptor<Long> accountIdCaptor;
  @Captor ArgumentCaptor<ProjectRegisterDto> projectRegisterDtoCaptor;
  @Captor ArgumentCaptor<List<ProjectUpdateDto>> projectUpdateDtoListCaptor;
  @Captor ArgumentCaptor<Long> projectIdCaptor;

  @Spy @InjectMocks ProjectApi api;

  @BeforeEach
  public void setUp() {
    createApiMock(api);
  }

  @Test
  public void get() throws Exception {
    // mock
    var project = new Project();
    doReturn(project).when(projectService).get(projectIdCaptor.capture());
    var dto = new ProjectIdentificationDto();
    dto.projectId = 1L;

    // test
    var result = api.get(dto, req, res);

    // assert
    assertThat(result).isEqualTo(project);
    assertThat(projectIdCaptor.getValue()).isEqualTo(dto.projectId);
  }

  @Test
  public void list() throws Exception {
    // mock
    doReturn(1L).when(api).getAccountId(req);
    var list = List.of(new Project());
    doReturn(list).when(projectService).getList(accountIdCaptor.capture());

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
    var dto = new ProjectRegisterDto();
    doNothing()
        .when(projectService)
        .register(accountIdCaptor.capture(), projectRegisterDtoCaptor.capture());

    // test
    var result = api.register(dto, req, res);

    // assert
    if (result instanceof MessageDto messageDto) {
      assertThat(messageDto.message).isEqualTo("プロジェクトを登録しました。");
    } else {
      fail();
    }
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
    assertThat(projectRegisterDtoCaptor.getValue()).isEqualTo(dto);
  }

  @Test
  public void update() throws Exception {
    // mock
    doReturn(1L).when(api).getAccountId(req);
    doNothing()
        .when(projectService)
        .update(accountIdCaptor.capture(), projectUpdateDtoListCaptor.capture());

    var list = List.of(new ProjectUpdateDto());
    // test
    var result = api.update(list, req, res);

    // assert
    if (result instanceof MessageDto messageDto) {
      assertThat(messageDto.message).isEqualTo("プロジェクトを更新しました。");
    } else {
      fail();
    }
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
    assertThat(projectUpdateDtoListCaptor.getValue()).isEqualTo(list);
  }
}
