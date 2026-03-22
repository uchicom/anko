// (C) 2025 uchicom
package com.uchicom.pj.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import com.uchicom.pj.AbstractTest;
import com.uchicom.pj.dao.ProjectDao;
import com.uchicom.pj.dto.request.project.ProjectRegisterDto;
import com.uchicom.pj.dto.request.project.ProjectUpdateDto;
import com.uchicom.pj.entity.Project;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Spy;

/**
 * {@link ProjectService}のテストケース.
 *
 * @author uchicom
 */
@Tag("service")
public class ProjectServiceTest extends AbstractTest {

  @Mock ProjectDao projectDao;

  @Captor ArgumentCaptor<Project> projectCaptor;
  @Captor ArgumentCaptor<Long> accountIdCaptor;
  @Captor ArgumentCaptor<Long> projectIdCaptor;

  @Spy @InjectMocks ProjectService service;

  @Test
  public void get() {
    var project = new Project();
    doReturn(project).when(projectDao).findById(projectIdCaptor.capture());
    var projectId = 1L;

    // test
    var result = service.get(projectId);

    // assert
    assertThat(result).isEqualTo(project);
    assertThat(projectIdCaptor.getValue()).isEqualTo(projectId);
  }

  @Test
  public void getList() {
    var list = List.of(new Project());
    doReturn(list).when(projectDao).findByAccountId(accountIdCaptor.capture());

    // test
    var result = service.getList(1L);

    // assert
    assertThat(result).isEqualTo(list);
    assertThat(accountIdCaptor.getValue()).isEqualTo(1L);
  }

  @Test
  public void register() {
    doReturn(true).when(projectDao).insert(projectCaptor.capture());
    var dto = new ProjectRegisterDto();
    dto.customerId = 10L;
    dto.subject = "subject";
    dto.description = "description";
    dto.startScheduleDate = LocalDate.of(2025, 1, 1);
    dto.endScheduleDate = LocalDate.of(2025, 12, 31);

    // test
    service.register(1L, dto);

    // assert
    var project = projectCaptor.getValue();
    assertThat(project.account_id).isEqualTo(1L);
    assertThat(project.customer_id).isEqualTo(dto.customerId);
    assertThat(project.subject).isEqualTo(dto.subject);
    assertThat(project.description).isEqualTo(dto.description);
    assertThat(project.start_schedule_date).isEqualTo(dto.startScheduleDate);
    assertThat(project.end_schedule_date).isEqualTo(dto.endScheduleDate);
  }

  @Test
  public void update() {
    var project2 = new Project();
    project2.subject = "subject2";
    project2.description = "description2";
    project2.customer_id = 20L;
    var project3 = new Project();
    project3.subject = "subject3";
    project3.description = "description3";
    project3.customer_id = 30L;
    doReturn(null)
        .doReturn(project2)
        .doReturn(project3)
        .when(projectDao)
        .findByIdAndAccountId(projectIdCaptor.capture(), accountIdCaptor.capture());
    doReturn(true).when(projectDao).update(projectCaptor.capture());

    var dto1 = new ProjectUpdateDto();
    dto1.projectId = 1L;
    dto1.customerId = 10L;
    dto1.subject = "subject1";
    dto1.description = "description1";

    var dto2 = new ProjectUpdateDto();
    dto2.projectId = 2L;
    dto2.customerId = 20L;
    dto2.subject = "subject2";
    dto2.description = "description2";

    var dto3 = new ProjectUpdateDto();
    dto3.projectId = 3L;
    dto3.customerId = 31L;
    dto3.subject = "subject31";
    dto3.description = "description31";
    dto3.startScheduleDate = LocalDate.of(2025, 4, 1);
    dto3.endScheduleDate = LocalDate.of(2025, 9, 30);

    // test
    service.update(4L, List.of(dto1, dto2, dto3));

    // assert
    var projectIds = projectIdCaptor.getAllValues();
    assertThat(projectIds).hasSize(3);
    assertThat(projectIds.get(0)).isEqualTo(dto1.projectId);
    assertThat(projectIds.get(1)).isEqualTo(dto2.projectId);
    assertThat(projectIds.get(2)).isEqualTo(dto3.projectId);

    var accountIds = accountIdCaptor.getAllValues();
    assertThat(accountIds).hasSize(3);
    assertThat(accountIds.get(0)).isEqualTo(4L);
    assertThat(accountIds.get(1)).isEqualTo(4L);
    assertThat(accountIds.get(2)).isEqualTo(4L);

    assertThat(projectCaptor.getValue()).isEqualTo(project3);
    assertThat(project3.subject).isEqualTo("subject31");
    assertThat(project3.description).isEqualTo("description31");
    assertThat(project3.customer_id).isEqualTo(dto3.customerId);
    assertThat(project3.start_schedule_date).isEqualTo(dto3.startScheduleDate);
    assertThat(project3.end_schedule_date).isEqualTo(dto3.endScheduleDate);
  }
}
