// (C) 2025 uchicom
package com.uchicom.pj.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import com.uchicom.pj.AbstractTest;
import com.uchicom.pj.dao.TaskDao;
import com.uchicom.pj.dto.request.task.TaskRegisterDto;
import com.uchicom.pj.dto.request.task.TaskUpdateDto;
import com.uchicom.pj.entity.Task;
import java.time.LocalDateTime;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

/**
 * {@link TaskService}のテストケース.
 *
 * @author uchicom
 */
@Tag("service")
public class TaskServiceTest extends AbstractTest {

  @Mock TaskDao taskDao;

  @Captor ArgumentCaptor<Task> taskCaptor;
  @Captor ArgumentCaptor<Long> taskIdCaptor;
  @Captor ArgumentCaptor<Long> projectIdCaptor;

  @Spy @InjectMocks TaskService service;

  @Test
  public void getList() {
    var list = List.of(new Task());
    doReturn(list).when(taskDao).findByProjectId(projectIdCaptor.capture());

    // test
    var result = service.getList(1L);

    // assert
    assertThat(result).isEqualTo(list);
    assertThat(projectIdCaptor.getValue()).isEqualTo(1L);
  }

  @Test
  public void register() {
    doReturn(true).when(taskDao).insert(taskCaptor.capture());
    var dto = new TaskRegisterDto();
    dto.projectId = 10L;
    dto.subject = "subject";
    dto.priority = 1;
    dto.cost = 100.0;
    dto.progress = 0;
    dto.startDatetime = LocalDateTime.of(2025, 1, 1, 9, 0);
    dto.description = "description";

    // test
    service.register(dto);

    // assert
    var task = taskCaptor.getValue();
    assertThat(task.project_id).isEqualTo(dto.projectId);
    assertThat(task.subject).isEqualTo(dto.subject);
    assertThat(task.priority).isEqualTo(dto.priority);
    assertThat(task.cost).isEqualTo(dto.cost);
    assertThat(task.progress).isEqualTo(dto.progress);
    assertThat(task.start_datetime).isEqualTo(dto.startDatetime);
    assertThat(task.description).isEqualTo(dto.description);
  }

  @Test
  public void update() {
    var task2 = new Task();
    task2.subject = "subject2";
    task2.project_id = 20L;
    var task3 = new Task();
    task3.subject = "subject3";
    task3.project_id = 30L;
    doReturn(null).doReturn(task2).doReturn(task3).when(taskDao).findById(taskIdCaptor.capture());
    doReturn(true).when(taskDao).update(taskCaptor.capture());

    var dto1 = new TaskUpdateDto();
    dto1.taskId = 1L;
    dto1.projectId = 10L;
    dto1.subject = "subject1";

    var dto2 = new TaskUpdateDto();
    dto2.taskId = 2L;
    dto2.projectId = 20L;
    dto2.subject = "subject2";

    var dto3 = new TaskUpdateDto();
    dto3.taskId = 3L;
    dto3.projectId = 31L;
    dto3.subject = "subject31";
    dto3.progress = 50;
    dto3.completeDatetime = LocalDateTime.of(2025, 12, 31, 18, 0);

    // test
    service.update(List.of(dto1, dto2, dto3));

    // assert
    var taskIds = taskIdCaptor.getAllValues();
    assertThat(taskIds).hasSize(3);
    assertThat(taskIds.get(0)).isEqualTo(dto1.taskId);
    assertThat(taskIds.get(1)).isEqualTo(dto2.taskId);
    assertThat(taskIds.get(2)).isEqualTo(dto3.taskId);

    assertThat(taskCaptor.getValue()).isEqualTo(task3);
    assertThat(task3.subject).isEqualTo("subject31");
    assertThat(task3.progress).isEqualTo(50);
    assertThat(task3.complete_datetime).isEqualTo(dto3.completeDatetime);
  }
}
