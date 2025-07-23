// (C) 2023 uchicom
package com.uchicom.tracker.dao;

import static org.assertj.core.api.Assertions.assertThat;

import com.uchicom.tracker.entity.Issue;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;

/**
 * {@link IssueDao}のテストケース.
 *
 * @author shigeki.uchiyama
 */
@Tag("dao")
public class IssueDaoTest extends AbstractDaoTest<Issue, IssueDao> {

  public IssueDaoTest() {
    super(Issue::new, helper -> new IssueDao(helper));
  }

  /** {@link IssueDao#findAll()}のテスト. */
  @Test
  public void findAll() throws Exception {
    test(
        () -> {
          dao.findAll();
          assertThat(getSQL()).isEqualTo("SELECT * FROM tracker.issue ORDER BY id");
        });
  }

  /** {@link IssueDao#findByAccountId(long)}のテスト. */
  @Test
  public void findByAccountId() throws Exception {
    test(
        () -> {
          dao.findByAccountId(1L);
          assertThat(getSQL()).isEqualTo("SELECT * FROM tracker.issue WHERE account_id = 1");
        });
  }

  /** {@link IssueDao#findByIdAndAccountId(long)}のテスト. */
  @Test
  public void findByIdAndAccountId() throws Exception {
    test(
        () -> {
          dao.findByIdAndAccountId(1L, 2L);
          assertThat(getSQL())
              .isEqualTo("SELECT * FROM tracker.issue WHERE id = 1 AND account_id = 2");
        });
  }
}
