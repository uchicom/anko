// (C) 2025 uchicom
package com.uchicom.pj.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.uchicom.pj.entity.Project;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * {@link ProjectDao}のテストケース.
 *
 * @author uchicom
 */
@Tag("dao")
public class ProjectDaoTest extends AbstractDaoTest<Project, ProjectDao> {

  public ProjectDaoTest() {
    super(Project::new, helper -> new ProjectDao(helper));
  }

  /** {@link ProjectDao#findAll()}のテスト. */
  @Test
  public void findAll() throws Exception {
    test(
        () -> {
          dao.findAll();
          assertThat(getSQL()).isEqualTo("SELECT * FROM pj.project ORDER BY id");
        });
  }

  /** {@link ProjectDao#findByAccountId(long)}のテスト. */
  @Test
  public void findByAccountId() throws Exception {
    test(
        () -> {
          dao.findByAccountId(1L);
          assertThat(getSQL()).isEqualTo("SELECT * FROM pj.project WHERE account_id = 1");
        });
  }

  /** {@link ProjectDao#findByIdAndAccountId(long, long)}のテスト. */
  @Test
  public void findByIdAndAccountId() throws Exception {
    test(
        () -> {
          dao.findByIdAndAccountId(1L, 2L);
          assertThat(getSQL())
              .isEqualTo("SELECT * FROM pj.project WHERE id = 1 AND account_id = 2");
        });
  }
}
