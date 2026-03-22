// (C) 2025 uchicom
package com.uchicom.pj.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.uchicom.pj.entity.TaskToAccount;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * {@link TaskToAccountDao}のテストケース.
 *
 * @author uchicom
 */
@Tag("dao")
public class TaskToAccountDaoTest extends AbstractDaoTest<TaskToAccount, TaskToAccountDao> {

  public TaskToAccountDaoTest() {
    super(TaskToAccount::new, helper -> new TaskToAccountDao(helper));
  }

  /** {@link TaskToAccountDao#findAll()}のテスト. */
  @Test
  public void findAll() throws Exception {
    test(
        () -> {
          dao.findAll();
          assertThat(getSQL()).isEqualTo("SELECT * FROM pj.task_to_account ORDER BY id");
        });
  }

  /** {@link TaskToAccountDao#findByTaskId(long)}のテスト. */
  @Test
  public void findByTaskId() throws Exception {
    test(
        () -> {
          dao.findByTaskId(1L);
          assertThat(getSQL()).isEqualTo("SELECT * FROM pj.task_to_account WHERE task_id = 1");
        });
  }

  /** {@link TaskToAccountDao#findByAccountId(long)}のテスト. */
  @Test
  public void findByAccountId() throws Exception {
    test(
        () -> {
          dao.findByAccountId(2L);
          assertThat(getSQL()).isEqualTo("SELECT * FROM pj.task_to_account WHERE account_id = 2");
        });
  }
}
