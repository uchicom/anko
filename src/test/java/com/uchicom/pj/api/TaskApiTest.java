// (C) 2025 uchicom
package com.uchicom.pj.api;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doReturn;

import com.uchicom.pj.AbstractTest;
import com.uchicom.pj.dto.request.task.TaskListDto;
import com.uchicom.pj.dto.request.task.TaskRegisterDto;
import com.uchicom.pj.dto.request.task.TaskUpdateDto;
import com.uchicom.pj.dto.response.ListDto;
import com.uchicom.pj.dto.response.MessageDto;
import com.uchicom.pj.entity.Task;
import com.uchicom.pj.service.TaskService;
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
public class TaskApiTest extends AbstractTest {
  @Mock HttpServletRequest req;
  @Mock HttpServletResponse res;

  @Mock TaskService taskService;

  @Captor ArgumentCaptor<Long> projectIdCaptor;
  @Captor ArgumentCaptor<TaskRegisterDto> taskRegisterDtoCaptor;
  @Captor ArgumentCaptor<List<TaskUpdateDto>> taskUpdateDtoListCaptor;

  @Spy @InjectMocks TaskApi api;

  @BeforeEach
  public void setUp() {
    createApiMock(api);
  }

  @Test
  public void list() throws Exception {
    // mock
    var list = List.of(new Task());
    doReturn(list).when(taskService).getList(projectIdCaptor.capture());
    var dto = new TaskListDto();
    dto.projectId = 1L;

    // test
    var result = api.list(dto, req, res);

    // assert
    if (result instanceof ListDto<?> listDto) {
      assertThat(listDto.list).isEqualTo(list);
    } else {
      fail();
    }
    assertThat(projectIdCaptor.getValue()).isEqualTo(dto.projectId);
  }

  @Test
  public void register() throws Exception {
    // mock
    var dto = new TaskRegisterDto();
    doNothing().when(taskService).register(taskRegisterDtoCaptor.capture());

    // test
    var result = api.register(dto, req, res);

    // assert
    if (result instanceof MessageDto messageDto) {
      assertThat(messageDto.message).isEqualTo("タスクを登録しました。");
    } else {
      fail();
    }
    assertThat(taskRegisterDtoCaptor.getValue()).isEqualTo(dto);
  }

  @Test
  public void update() throws Exception {
    // mock
    doNothing().when(taskService).update(taskUpdateDtoListCaptor.capture());

    var list = List.of(new TaskUpdateDto());
    // test
    var result = api.update(list, req, res);

    // assert
    if (result instanceof MessageDto messageDto) {
      assertThat(messageDto.message).isEqualTo("タスクを更新しました。");
    } else {
      fail();
    }
    assertThat(taskUpdateDtoListCaptor.getValue()).isEqualTo(list);
  }
}
